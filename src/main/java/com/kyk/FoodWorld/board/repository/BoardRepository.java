package com.kyk.FoodWorld.board.repository;


import com.kyk.FoodWorld.board.domain.dto.BoardSearchCond;
import com.kyk.FoodWorld.board.domain.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;


/**
 * 구현체가 바뀌어도 같은 기능들을 선언하게 인터페이스를 만듬
 */
public interface BoardRepository {
    /**
     * 글 저장
     */
    Board save(Board board);


    /**
     * 글 id로 조회
     */
    Optional<Board> findById(Long id);
    List<Board> findByMemberId(Long memberId);

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
    Page<Board> findByTitleContainingAndBoardTypeContaining(String titleSearchKeyword, Pageable pageable, String boardType);
    Page<Board> findByWriterContainingAndBoardTypeContaining(String writerSearchKeyword, Pageable pageable, String boardType);
    Page<Board> findByTitleContainingAndWriterContainingAndBoardTypeContaining(String titleSearchKeyword, String writerSearchKeyword, Pageable pageable, String boardType);


    /**
     * 최근 boardId 조회
     */
    Long findFirstCursorBoardId(String boardType);
    Long findFirstCursorBoardIdInMember(Long memberId, String boardType);

    /**
     * 무한 스크롤 페이징
     */
    Slice<Board> searchBySlice(String memberId, Long lastBoardId, Boolean first, Pageable pageable, String boardType);
    Slice<Board> searchBySliceByWriter(String memberId, Long lastCursorBoardId, Boolean first, String writerSearchKeyword, Pageable pageable, String boardType);

    /**
     * 글 삭제
     */
    void delete(Long boardId);

    /**
     * 조회수 카운트
     */
    int updateCount(Long boardId);

    /**
     * 한 회원이 작성한 게시글 수 카운트
     */
    int boardsTotalCount(Long memberId);


    List<Board> popularBoardList(String boardType);

    Page<Board> categoryBoardList(String boardType, BoardSearchCond boardSearchDto, Pageable pageable);

}
