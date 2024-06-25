package com.kyk.FoodWorld.board.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
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
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;


    /**
     * 게시글 저장
     */
    @Override
    public void upload(Long memberId, UploadFormBase boardDto) throws IOException {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("글 등록 실패: 로그인 상태가 아닙니다." + memberId));

        Board boardEntity = boardDto.toSaveEntity(findMember);

        // 첨부파일까지 지원하는 자유게시판과 메뉴추천게시판의 전송 객체인 경우
        if (boardDto instanceof BoardUploadForm) {
            BoardUploadForm childBoardDto = (BoardUploadForm) boardDto;
            List<MultipartFile> attachFiles = childBoardDto.getAttachFiles();

            // 첨부파일이 있을 경우
            if (!attachFiles.isEmpty() && !attachFiles.get(0).getOriginalFilename().isBlank()) {
                boardEntity.updateFileAttached(1);
                fileUpload(attachFiles, boardEntity, "attachFile/", "attached");
            }
        }

        List<MultipartFile> imageFiles = boardDto.getImageFiles();
        // 이미지 파일이 있을 경우
        if (!imageFiles.isEmpty() && !imageFiles.get(0).getOriginalFilename().isBlank()) {
            boardEntity.updateFileAttached(1);
            fileUpload(imageFiles, boardEntity, "imageFile/", "none");
        }

        boardRepository.save(boardEntity);
    }

    private void fileUpload(List<MultipartFile> files, Board board, String folder, String attachedType) throws IOException {
        // 루프를 돌려 파일을 모두 찾고 반환
        for (MultipartFile file: files) {
            String originalFilename = file.getOriginalFilename();

            // 파일에 이름을 붙일 랜덤으로 식별자 지정
            UUID uuid = UUID.randomUUID();
            String storedFileName = uuid + "_" + originalFilename;

            // S3 버킷에 넣을 파일 설정 및 넣기
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(bucket, folder + storedFileName, file.getInputStream(), metadata);

            // DB에 파일 관련 필드 값 저장
            BoardFile boardFileEntity = BoardFile.toBoardFileEntity(board, originalFilename, storedFileName, amazonS3.getUrl(bucket, "imageFile/"+storedFileName).toString(),attachedType);
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
    public Long updateBoard(Long boardId, UpdateFormBase updateForm) throws IOException {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("게시글 가져오기 실패: 게시글을 찾지 못했습니다." + boardId));

        List<BoardFile> findBoardFiles = boardFileRepository.findByBoard(findBoard);

        // 각 게시판의 DTO가 달라서 게시판별로 처리
        if (updateForm instanceof FreeBoardUpdateForm) {
            FreeBoardUpdateForm freeBoardUpdateForm = (FreeBoardUpdateForm)updateForm;

            // 새로 받아온 파일 생성
            List<MultipartFile> attachFiles = freeBoardUpdateForm.getAttachFiles();
            List<MultipartFile> imageFiles = freeBoardUpdateForm.getImageFiles();
            log.info("첨부파일명={}", attachFiles.get(0).getOriginalFilename());
            log.info("이미지파일명={}", imageFiles.get(0).getOriginalFilename());

            // 새로 받아온 첨부파일이 있을 경우
            if (!attachFiles.get(0).getOriginalFilename().isBlank()) {
                // 파일이 없었던 게시글인 경우가 있을 수 있으므로 파일 여부 필드 업데이트
                findBoard.updateFileAttached(1);

                // 기존 파일과 게시글 엔티티 삭제 처리
                pastFileDelete(findBoardFiles, "attached", "attachFile/");

                // 아마존에 새로 받아온 첨부파일 생성 및 파일 엔티티 생성
                fileUpload(attachFiles, findBoard, "attachFile/", "attached");
            }

            // 새로 받아온 이미지파일이 있을 경우
            if (!imageFiles.get(0).getOriginalFilename().isBlank()) {
                findBoard.updateFileAttached(1);

                pastFileDelete(findBoardFiles, "none", "imageFile/");

                fileUpload(imageFiles, findBoard, "imageFile/", "none");
            }

            // 수정한 게시글 엔티티 필드 적용
            findBoard.updateBoard(freeBoardUpdateForm.getTitle(), freeBoardUpdateForm.getContent(), freeBoardUpdateForm.getSubType());
        } else if (updateForm instanceof RecommendBoardUpdateForm) {
            RecommendBoardUpdateForm recommendBoardUpdateForm = (RecommendBoardUpdateForm)updateForm;

            List<MultipartFile> attachFiles = recommendBoardUpdateForm.getAttachFiles();
            List<MultipartFile> imageFiles = recommendBoardUpdateForm.getImageFiles();
            log.info("첨부파일명={}", attachFiles.get(0).getOriginalFilename());
            log.info("이미지파일명={}", imageFiles.get(0).getOriginalFilename());

            if (!attachFiles.get(0).getOriginalFilename().isBlank()) {
                findBoard.updateFileAttached(1);

                pastFileDelete(findBoardFiles, "attached", "attachFile/");

                fileUpload(attachFiles, findBoard, "attachFile/", "attached");
            }

            if (!imageFiles.get(0).getOriginalFilename().isBlank()) {
                findBoard.updateFileAttached(1);
                
                pastFileDelete(findBoardFiles, "none", "imageFile/");

                fileUpload(imageFiles, findBoard, "imageFile/", "none");
            }

            findBoard.updateRecommendBoard(recommendBoardUpdateForm.getTitle(), recommendBoardUpdateForm.getContent(), recommendBoardUpdateForm.getSubType(), recommendBoardUpdateForm.getArea(), recommendBoardUpdateForm.getMenuName());
        } else {
            MuckstarUpdateForm muckstarUpdateForm = (MuckstarUpdateForm)updateForm;

            List<MultipartFile> imageFiles = muckstarUpdateForm.getImageFiles();
            log.info("이미지파일명={}", imageFiles.get(0).getOriginalFilename());

            if (!imageFiles.get(0).getOriginalFilename().isBlank()) {
                pastFileDelete(findBoardFiles, "none", "imageFile/");

                fileUpload(imageFiles, findBoard, "imageFile/", "none");
            }

            findBoard.updateBoard(null, muckstarUpdateForm.getContent(), muckstarUpdateForm.getSubType());
        }

        log.info("수정완료");
        return findBoard.getId();
    }

    private void pastFileDelete(List<BoardFile> findBoardFiles, String attached, String folder) {
        for (BoardFile boardFile : findBoardFiles) {
            if (boardFile.getAttachedType().equals(attached)) {
                // 삭제 대상 객체 생성 및 삭제
                deleteFile(folder + boardFile.getStoredFileName());

                // 기존 게시글 엔티티 삭제
                boardFileRepository.delete(boardFile);
            }
        }
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
                    deleteFile("attachFile/" + boardFile.getStoredFileName());
                } else {
                    deleteFile("imageFile/" + boardFile.getStoredFileName());
                }
            }
        } else {
            for (BoardFile boardFile : findBoardFiles) {
                deleteFile("imageFile/" + boardFile.getStoredFileName());
            }
        }

        boardRepository.delete(boardId);
    }

    private void deleteFile(String key) {
        // 삭제 대상 객체 생성
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);

        // 삭제 처리
        amazonS3.deleteObject(deleteObjectRequest);
    }

    @Override
    public ResponseEntity<UrlResource> fileDownload(BoardFile boardFile) throws IOException {
        // S3 버킷에서 해당 관련 파일이 있는지 조회하여 객체로 담기
        S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, "attachFile/" + boardFile.getStoredFileName()));
        log.info("파일 정상 받아졌나? = {}", o.getBucketName());

        // S3에 올라간 파일은 amazonS3.getUrl(버킷이름, 파일이름)을 통해 UrlResource에 담아 파일 다운로드를 할 수 있다.
        UrlResource resource = new UrlResource(amazonS3.getUrl(bucket, o.getKey()));

        // 받을 파일명을 한글 깨짐 방지를 위해 한글로 확실히 변환시키고 넣기
        String encodeUploadFileName = UriUtils.encode(boardFile.getOriginalFileName(), StandardCharsets.UTF_8);

        // CONTENT_DISPOSITION이 attachment에 filename이 맞으면 다운로드하게 한다.
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
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