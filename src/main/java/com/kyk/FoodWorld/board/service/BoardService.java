package com.kyk.FoodWorld.board.service;


import com.kyk.FoodWorld.board.domain.dto.*;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.domain.entity.BoardFile;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface BoardService {

    /**
     * 글 저장
     */
    void upload(Long memberId, UploadFormBase boardDto) throws IOException;

    /**
     * 글 수정
     */
    Long updateBoard(Long boardId, UpdateFormBase updateForm) throws IOException;

    /**
     * 글 id로 조회
     */
    Optional<Board> findById(Long id);

    /**
     * 글 모두 조회
     */
    List<Board> findAll();

    /**
     * 글 페이징 처리
     */
    Page<Board> findPageListByBoardType(Pageable pageable, String boardType);

    /**
     * 글 페이징 + 검색
     */
    Page<Board> findByTitleContaining(String titleSearchKeyword, Pageable pageable, String boardType);

    Page<Board> findByWriterContaining(String writerSearchKeyword, Pageable pageable, String boardType);

    Page<Board> findByTitleContainingAndWriterContaining(String titleSearchKeyword, String writerSearchKeyword, Pageable pageable, String boardType);

    /**
     * 마지막 id 조회
     */
    Long findFirstCursorBoardId(String boardType);
    Long findFirstCursorBoardIdInMember(Long memberId, String boardType);

    /**
     * 무한 스크롤 페이징 처리
     */
    Slice<Board> searchBySlice(String memberId, Long lastBoardId, Boolean first, Pageable pageable, String boardType);
    Slice<Board> searchBySliceByWriter(String memberId, Long lastCursorBoardId, Boolean first, String writerSearchKeyword, Pageable pageable, String boardType);
    
    /**
     * 글 삭제
     */
    void delete(Long boardId, String boardType);


    /**
     * 글 조회수 카운트
     */
    int updateCount(Long boardId);

    /**
     * 한 회원이 작성한 게시글 수 카운트
     */
    int boardsTotalCount(Long memberId);

    /**
     * 좋아요 개수 최신화
     */
    void updateLikeCount(Long boardId, int likeCount);


    /**
     * 댓글 개수 최신화
     */
    Long updateCommentCount(Long boardId);


    List<Board> popularBoardList(String boardType);

    Page<Board> categoryBoardList(String boardType, BoardSearchCond boardSearchDto, Pageable pageable);


    /**
     * 파일 다운로드
     */
    ResponseEntity<UrlResource> fileDownload(BoardFile boardFile) throws IOException;
}


