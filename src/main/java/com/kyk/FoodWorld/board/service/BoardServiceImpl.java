package com.kyk.FoodWorld.board.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kyk.FoodWorld.board.domain.dto.*;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.domain.entity.BoardFile;
import com.kyk.FoodWorld.board.repository.BoardFileRepository;
import com.kyk.FoodWorld.board.repository.BoardRepository;
import com.kyk.FoodWorld.comment.repository.CommentRepository;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


/**
 * 서비스를 스프링 JPA 구현체로 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Value("${file.imageFileLocation}")
    private String imageFileLocation;

    @Value("${file.attachFileLocation}")
    private String attachFileLocation;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    @Override
    public Long upload(Long memberId, BoardUploadForm boardDto) throws IOException {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("글 등록 실패: 로그인 상태가 아닙니다." + memberId));

        List<MultipartFile> attachFiles = boardDto.getAttachFiles();
        List<MultipartFile> imageFiles = boardDto.getImageFiles();

        // 첨부파일이 있을 경우
        if (!attachFiles.get(0).getOriginalFilename().isBlank()) {
            // 여러 개의 파일일 수 있으므로 부모 객체인 Board부터 가져와야함
            // + attached 속성 1로 설정한 toSaveFileEntity로 글 저장
            Board boardEntity = boardDto.toSaveFileEntity(findMember, boardDto);
            Long savedId = boardRepository.save(boardEntity).getId();
            Board board = boardRepository.findById(savedId).get();

            attachUpload(boardDto, board);

            // + 이미지파일이 있을 경우
            if (imageFiles.get(0).getOriginalFilename() != null && !imageFiles.get(0).getOriginalFilename().isBlank()) {
                imageUpload(boardDto, board);
            }

            return savedId;
        } else if (attachFiles.get(0).getOriginalFilename().isBlank()) {
            // 첨부파일이 없고 이미지파일은 있을 경우
            if (!imageFiles.get(0).getOriginalFilename().isBlank()) {
                Board boardEntity = boardDto.toSaveFileEntity(findMember, boardDto);
                Long savedId = boardRepository.save(boardEntity).getId();
                Board board = boardRepository.findById(savedId).get();

                imageUpload(boardDto, board);
                return savedId;
            } else { // 첨부파일, 이미지파일 모두 없을 경우
                Board uploadBoard = uploadBoard(boardDto, findMember);
                return uploadBoard.getId();
            }
        }
        return null;
    }

    @Override
    public Long muckstarUpload(Long memberId, MucstarUploadForm boardDto) throws IOException {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("글 등록 실패: 로그인 상태가 아닙니다." + memberId));

        List<MultipartFile> imageFiles = boardDto.getImageFiles();

        try {
            if (!imageFiles.get(0).getOriginalFilename().isBlank()) {
                // + attached 속성 1로 설정한 toSaveFileEntity로 글 저장
                Board boardEntity = boardDto.toSaveFileEntity(findMember, boardDto);

                Long savedId = boardRepository.save(boardEntity).getId();
                Board board = boardRepository.findById(savedId).get();

                muckstarImageUpload(boardDto, board);
            }
        } catch(IllegalStateException e) {
            log.info("먹스타그램은 이미지가 필수라고 안내 오류");
        }

        return null;
    }


    private Board uploadBoard(BoardUploadForm boardDto, Member findMember) {
        Board board = boardDto.toSaveEntity(findMember, boardDto);
        Board uploadBoard = boardRepository.save(board);
        log.info("uploadBoard = {}", board);
        return uploadBoard;
    }

    private void imageUpload(BoardUploadForm boardDto, Board board) throws IOException {
        // 루프를 돌려 파일을 모두 찾고 반환
        for (MultipartFile imageFiles: boardDto.getImageFiles()) {
            String originalFilename = imageFiles.getOriginalFilename();

            // 파일에 이름을 붙일 랜덤으로 식별자 지정
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "_" + originalFilename;

            // S3 버킷에 넣을 파일 설정 및 넣기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFiles.getSize());
            metadata.setContentType(imageFiles.getContentType());

            amazonS3.putObject(bucket, "imageFile/"+storedFileName, imageFiles.getInputStream(), metadata);

            String attachedType = "none";

            // DB에 파일 관련 필드 값 저장
            BoardFile boardFileEntity = BoardFile.toBoardFileEntity(board, originalFilename, storedFileName, attachedType);
            boardFileRepository.save(boardFileEntity);
        }
    }
    private void muckstarImageUpload(MucstarUploadForm boardDto, Board board) throws IOException {
        // 루프를 돌려 파일을 모두 찾고 반환
        for (MultipartFile imageFiles: boardDto.getImageFiles()) {
            String originalFilename = imageFiles.getOriginalFilename();

            // 파일에 이름을 붙일 랜덤으로 식별자 지정
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "_" + originalFilename;

            // S3 버킷에 넣을 파일 설정 및 넣기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageFiles.getSize());
            metadata.setContentType(imageFiles.getContentType());

            amazonS3.putObject(bucket, "imageFile/"+storedFileName, imageFiles.getInputStream(), metadata);

            String attachedType = "none";

            // DB에 파일 관련 필드 값 저장
            BoardFile boardFileEntity = BoardFile.toBoardFileEntity(board, originalFilename, storedFileName, attachedType);
            boardFileRepository.save(boardFileEntity);
        }
    }

    private void attachUpload(BoardUploadForm boardDto, Board board) throws IOException {
        // 루프를 돌려 파일을 모두 찾고 반환
        for (MultipartFile attachFiles: boardDto.getAttachFiles()) {
            String originalFilename = attachFiles.getOriginalFilename();

            // 파일에 이름을 붙일 랜덤으로 식별자 지정
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "_" + originalFilename;

            // S3 버킷에 넣을 파일 설정 및 넣기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(attachFiles.getSize());
            metadata.setContentType(attachFiles.getContentType());

            amazonS3.putObject(bucket, "attachFile/"+storedFileName, attachFiles.getInputStream(), metadata);

            String attachedType = "attached";

            // DB에 파일 관련 필드 값 저장
            BoardFile boardFileEntity = BoardFile.toBoardFileEntity(board, originalFilename, storedFileName, attachedType);
            boardFileRepository.save(boardFileEntity);
        }
    }

//    // 확장자 구분 메서드
//    private String createStoreFileName(String originalFilename) {
//        String ext = extractedExt(originalFilename);
//        String uuid = UUID.randomUUID().toString();
//        return uuid + "." + ext;
//
//    }
//    private String extractedExt(String originalFilename) {
//        // 마지막 .뒤의 위치를 가져온다.
//        int pos = originalFilename.lastIndexOf(".");
//        // 위를 활용하여 확장자를 가져온다.
//        return originalFilename.substring(pos + 1);
//    }


    /**
     * 게시글 수정
     */
    @Override
    public Long updateBoard(Long boardId, FreeBoardUpdateForm freeBoardUpdateForm, RecommendBoardUpdateForm recommendBoardUpdateForm, MuckstarUpdateForm muckstarUpdateForm) throws IOException {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

        List<BoardFile> findBoardFiles = boardFileRepository.findByBoard(findBoard);

        // 각 게시판의 DTO가 달라서 게시판별로 처리
        // 자유게시판일 때
        if (findBoard.getBoardType().equals("자유게시판")) {
            // 새로 받아온 파일 생성
            List<MultipartFile> attachFiles = freeBoardUpdateForm.getAttachFiles();
            List<MultipartFile> imageFiles = freeBoardUpdateForm.getImageFiles();
            log.info("첨부파일명={}", attachFiles.get(0).getOriginalFilename());
            log.info("이미지파일명={}", imageFiles.get(0).getOriginalFilename());

            // 새로 받아온 첨부파일, 이미지 파일 모두 있을 경우
            if (!Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && !Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE1: 새로 받아온 첨부파일, 이미지 파일 모두 있을 경우");
                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 첨부파일일 때의 삭제 처리
                    if (boardFile.getAttachedType().equals("attached")) {
                        deleteFile(attachFileLocation, boardFile);
                    } else if (boardFile.getAttachedType().equals("none")){ // 이미지 파일일 때의 삭제 처리
                        // 기존 실제 이미지 파일 삭제
                        deleteFile(imageFileLocation, boardFile);
                    }
                }

                // 새로 받아온 첨부파일 생성
                saveNewBoardAndBoardFileDB(attachFiles, attachFileLocation, findBoard, freeBoardUpdateForm, null, null, "attached");

                // 새로 받아온 이미지 파일 생성
                saveNewBoardAndBoardFileDB(imageFiles, imageFileLocation, findBoard, freeBoardUpdateForm, null, null, "none");
            } else if (!Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE2: 새로 받아온 첨부 파일만 있을 경우");

                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 첨부 파일일 때의 기존 실제 첨부 파일 삭제 처리
                    if (boardFile.getAttachedType().equals("attached")){
                        deleteFile(attachFileLocation, boardFile);
                    }
                }

                // 새로 받아온 첨부파일만 생성
                saveNewBoardAndBoardFileDB(attachFiles, attachFileLocation, findBoard, freeBoardUpdateForm, null, null, "attached");
            } else if (Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && !Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE3: 새로 받아온 이미지 파일만 있을 경우");
                
                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 이미지 파일일 때의 기존 실제 이미지 파일 삭제 처리
                    if (boardFile.getAttachedType().equals("none")) {
                        deleteFile(imageFileLocation, boardFile);
                    }
                }

                // 새로 받아온 이미지 파일만 생성
                saveNewBoardAndBoardFileDB(imageFiles, imageFileLocation, findBoard, freeBoardUpdateForm, null, null, "none");
            } else {
                log.info("CASE4: 새로 받아온 파일이 아예 없을 경우");

                findBoard.updateBoard(freeBoardUpdateForm.getTitle(), freeBoardUpdateForm.getContent(), freeBoardUpdateForm.getSubType());
            }
        }
        else if (findBoard.getBoardType().equals("추천게시판")){ // 추천게시판인 경우
            // 새로 받아온 파일 생성
            List<MultipartFile> attachFiles = recommendBoardUpdateForm.getAttachFiles();
            List<MultipartFile> imageFiles = recommendBoardUpdateForm.getImageFiles();
            log.info("첨부파일명={}", attachFiles.get(0).getOriginalFilename());
            log.info("이미지파일명={}", imageFiles.get(0).getOriginalFilename());

            // 새로 받아온 첨부파일, 이미지 파일 모두 있을 경우
            if (!Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && !Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE1: 새로 받아온 첨부파일, 이미지 파일 모두 있을 경우");
                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 첨부파일일 때의 삭제 처리
                    if (boardFile.getAttachedType().equals("attached")) {
                        deleteFile(attachFileLocation, boardFile);
                    } else if (boardFile.getAttachedType().equals("none")){ // 이미지 파일일 때의 삭제 처리
                        // 기존 실제 이미지 파일 삭제
                        deleteFile(imageFileLocation, boardFile);
                    }
                }

                // 새로 받아온 첨부파일 생성
                saveNewBoardAndBoardFileDB(attachFiles, attachFileLocation, findBoard, null, recommendBoardUpdateForm, null, "attached");

                // 새로 받아온 이미지 파일 생성
                saveNewBoardAndBoardFileDB(imageFiles, imageFileLocation, findBoard, null,  recommendBoardUpdateForm, null, "none");
            } else if (!Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE2: 새로 받아온 첨부 파일만 있을 경우");

                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 첨부 파일일 때의 기존 실제 첨부 파일 삭제 처리
                    if (boardFile.getAttachedType().equals("attached")){
                        deleteFile(attachFileLocation, boardFile);
                    }
                }

                // 새로 받아온 첨부파일만 생성
                saveNewBoardAndBoardFileDB(attachFiles, attachFileLocation, findBoard, null, recommendBoardUpdateForm, null, "attached");
            } else if (Objects.requireNonNull(attachFiles.get(0).getOriginalFilename()).isBlank() && !Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                log.info("CASE3: 새로 받아온 이미지 파일만 있을 경우");

                // 기존 파일과 엔티티 삭제 처리
                for (BoardFile boardFile : findBoardFiles) {
                    // 이미지 파일일 때의 기존 실제 이미지 파일 삭제 처리
                    if (boardFile.getAttachedType().equals("none")) {
                        deleteFile(imageFileLocation, boardFile);
                    }
                }

                // 새로 받아온 이미지 파일만 생성
                saveNewBoardAndBoardFileDB(imageFiles, imageFileLocation, findBoard, null, recommendBoardUpdateForm,null,  "none");
            } else {
                log.info("CASE4: 새로 받아온 파일이 아예 없을 경우");

                findBoard.updateBoard(freeBoardUpdateForm.getTitle(), freeBoardUpdateForm.getContent(), freeBoardUpdateForm.getSubType());
            }
        }
        else { // 먹스타그램인 경우는 이미지 파일만 있으므로 이미지 파일만 처리
            List<MultipartFile> imageFiles = muckstarUpdateForm.getImageFiles();

            // 새로 받아온 이미지 파일이 있을 경우
            if (!Objects.requireNonNull(imageFiles.get(0).getOriginalFilename()).isBlank()) {
                for (BoardFile boardFile : findBoardFiles) {
                    // 기존 이미지 파일 삭제
                    deleteFile(imageFileLocation, boardFile);
                }

                // 새로 받아온 이미지 파일 생성
                saveNewBoardAndBoardFileDB(imageFiles, imageFileLocation, findBoard, null, null, muckstarUpdateForm, "none");
            } else { // 새로 받아온 파일이 없을 경우
                findBoard.updateBoard(muckstarUpdateForm.getTitle(), muckstarUpdateForm.getContent(), muckstarUpdateForm.getSubType());
            }
        }

        log.info("수정완료");
        return findBoard.getId();
    }

    private void saveNewBoardAndBoardFileDB(List<MultipartFile> files, String fileLocation, Board findBoard, FreeBoardUpdateForm freeBoardUpdateForm, RecommendBoardUpdateForm recommendBoardUpdateForm, MuckstarUpdateForm muckstarUpdateForm, String attached) throws IOException {
        log.info("BoardType={}", findBoard.getBoardType());

        if (!files.isEmpty()) {
            for (MultipartFile file : files) {
                // 새로 받아온 첨부파일 이름
                String attachOriginalFilename = file.getOriginalFilename();

                // 파일에 이름을 붙일 랜덤으로 식별자 지정
                UUID uuid = UUID.randomUUID();
                String attachStoredFileName = uuid + "_" + attachOriginalFilename;
                String savePath = fileLocation;

                // 새로 받아온 실제 첨부파일 디렉토리에 저장
                file.transferTo(new File(savePath, attachStoredFileName));

                // 각 게시글에 따른 엔티티와 파일 엔티티의 DB 업데이트
                if (findBoard.getBoardType().equals("자유게시판")) {
                    findBoard.updateBoard(freeBoardUpdateForm.getTitle(), freeBoardUpdateForm.getContent(), freeBoardUpdateForm.getSubType());
                }
                else if (findBoard.getBoardType().equals("추천게시판")){
                    findBoard.updateRecommendBoard(recommendBoardUpdateForm.getTitle(), recommendBoardUpdateForm.getContent(), recommendBoardUpdateForm.getSubType(), recommendBoardUpdateForm.getArea(), recommendBoardUpdateForm.getMenuName());
                } else {
                    findBoard.updateBoard(muckstarUpdateForm.getTitle(), muckstarUpdateForm.getContent(), muckstarUpdateForm.getSubType());
                }

                BoardFile updateBoardFileEntity = BoardFile.toBoardFileEntity(findBoard, attachOriginalFilename, attachStoredFileName, attached);
                boardFileRepository.save(updateBoardFileEntity);
            }
        }
    }

    private void deleteFile(String attachFileLocation, BoardFile boardFile) {
        // 기존 실제 첨부파일 삭제
        Path beforeAttachPath = Paths.get(attachFileLocation + "\\" + boardFile.getStoredFileName());
        try {
            Files.deleteIfExists(beforeAttachPath);
        } catch (DirectoryNotEmptyException e) {
            log.info("디렉토리가 비어있지 않습니다");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 기존 파일 엔티티 삭제
        boardFileRepository.delete(boardFile);
    }

    @Override
    public Long recommendUpdateBoard(Long boardId, RecommendBoardUpdateForm updateParam) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));
        log.info("서브 타입 = {}", findBoard.getSubType());
        findBoard.updateRecommendBoard(updateParam.getTitle(), updateParam.getContent(), updateParam.getSubType(), updateParam.getArea(), updateParam.getMenuName());

        log.info("수정완료");
        return findBoard.getId();
    }

    @Override
    public Long muckstarUpdateBoard(Long boardId, MuckstarUpdateForm updateParam) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));
        log.info("서브 타입 = {}", findBoard.getSubType());
        findBoard.updateBoard(updateParam.getTitle(), updateParam.getContent(), updateParam.getSubType());

        log.info("수정완료");
        return findBoard.getId();
    }

//    @Override
//    public Optional<BoardDto> findById(Long id) {
//        Optional<Board> optionalBoardEntity = boardRepository.findById(id);
//        if (optionalBoardEntity.isPresent()) {
//            Board boardEntity = optionalBoardEntity.get();
//            BoardDto boardDto = BoardDto.toBoardDto(boardEntity);
//            return boardDto;
//        } else {
//            return null;
//        }
////        return boardRepository.findById(id);
//    }

    @Override
    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    @Override
    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    @Override
    public Page<Board> findPageListByBoardType(Pageable pageable, String boardType) {
        return boardRepository.findPageListByBoardType(pageable, boardType);
    }

    @Override
    public Page<Board> findByTitleContaining(String titleSearchKeyword, Pageable pageable, String boardType) {
        log.info("제목 검색");

        return boardRepository.findByTitleContainingAndBoardTypeContaining(titleSearchKeyword, pageable, boardType);
    }

    @Override
    public Page<Board> findByWriterContaining(String writerSearchKeyword, Pageable pageable, String boardType) {
        log.info("작가 검색");

        return boardRepository.findByWriterContainingAndBoardTypeContaining(writerSearchKeyword, pageable, boardType);
    }

    @Override
    public Page<Board> findByTitleContainingAndWriterContaining(String titleSearchKeyword, String writerSearchKeyword, Pageable pageable, String boardType) {
        log.info("작가, 제목 동시 검색");

        return boardRepository.findByTitleContainingAndWriterContainingAndBoardTypeContaining(titleSearchKeyword, writerSearchKeyword, pageable, boardType);
    }

    @Override
    public Long findFirstCursorBoardId(String boardType) {
        return boardRepository.findFirstCursorBoardId(boardType);
    }
    @Override
    public Long findFirstCursorBoardIdInMember(Long memberId, String boardType) {
        return boardRepository.findFirstCursorBoardIdInMember(memberId, boardType);
    }

    @Override
    public Slice<Board> searchBySlice(String memberId, Long lastCursorBoardId, Boolean first, Pageable pageable, String boardType) {
        return boardRepository.searchBySlice(memberId, lastCursorBoardId, first, pageable, boardType);
    }

    @Override
    public Slice<Board> searchBySliceByWriter(String memberId, Long lastCursorBoardId, Boolean first, String writerSearchKeyword, Pageable pageable, String boardType) {
        return boardRepository.searchBySliceByWriter(memberId, lastCursorBoardId, first, writerSearchKeyword, pageable, boardType);
    }


    @Override
    public void delete(Long boardId, String boardType) {
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

        List<BoardFile> findBoardFiles = boardFileRepository.findByBoard(board);

        if (boardType.equals("자유게시판") || boardType.equals("추천게시판")) {
            for (BoardFile boardFile : findBoardFiles) {
                if (boardFile.getAttachedType().equals("attached")) {
                    // 삭제 대상 객체 생성
                    DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "attachFile/" + boardFile.getStoredFileName());

                    // 삭제 처리
                    amazonS3.deleteObject(deleteObjectRequest);
                } else {
                    DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "imageFile/" + boardFile.getStoredFileName());
                    amazonS3.deleteObject(deleteObjectRequest);
                }
            }
        } else {
            for (BoardFile boardFile : findBoardFiles) {
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, "imageFile/" + boardFile.getStoredFileName());
                amazonS3.deleteObject(deleteObjectRequest);
            }
        }

        boardRepository.delete(boardId);
    }

    @Override
    public int updateCount(Long boardId) {
        log.info("조회수 증가");

        return boardRepository.updateCount(boardId);
    }

    @Override
    public int boardsTotalCount(Long memberId) {
        return boardRepository.boardsTotalCount(memberId);
    }


    public void updateLikeCount(Long boardId, int likeCount) {
        Board findBoard = findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("좋아요 업데이트 실패: 게시글을 찾지 못했습니다." + boardId));

        findBoard.updateLikeCount(likeCount);

        log.info("좋아요 최신화 완료");
    }

    public Optional<BoardFile> findBoardFileById(Long boardFileId){
        return boardFileRepository.findById(boardFileId);
    }


    @Override
    public Long updateCommentCount(Long boardId) {
        Board findBoard = findById(boardId).orElseThrow();

        findBoard.updateCommentCount(commentRepository.findCommentCount(findBoard.getId()));

        log.info("댓글 개수 갱신완료");
        return findBoard.getId();
    }

    @Override
    public List<Board> popularBoardList(String boardType) {
        return boardRepository.popularBoardList(boardType);
    }

    @Override
    public Page<Board> categoryBoardList(String boardType, BoardSearchCond boardSearchDto, Pageable pageable) {
        return boardRepository.categoryBoardList(boardType, boardSearchDto, pageable);
    }

}