package com.kyk.FoodWorld.board.repository;


import com.kyk.FoodWorld.board.domain.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JPABoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
    /**
     * 키워드 검색에 따른 페이징
     */
    Page<Board> findPageListByBoardType(Pageable pageable, String boardType);
    Page<Board> findByTitleContainingAndBoardTypeContaining(String titleSearchKeyword, Pageable pageable, String boardType);
    Page<Board> findByWriterContainingAndBoardTypeContaining(String writerSearchKeyword, Pageable pageable, String boardType);
    Page<Board> findByTitleContainingAndWriterContainingAndBoardTypeContaining(String titleSearchKeyword, String writerSearchKeyword, Pageable pageable, String boardType);


    // 트랜잭션 스크립트 패턴
    /**
     * 해당 글 조회수 카운트
     */
    @Modifying // @Query 어노테이션에서 작성된 select를 제외한 insert, update, delete 쿼리 사용시 필요한 어노테이션
    @Query("update Board b set b.count = b.count + 1 where b.id = :boardId")
    int updateCount(Long boardId);


    /**
     * 한 회원이 작성한 게시글 수 카운트
     */
    @Query("select count(b) from Board b where b.member.id = :memberId")
    int boardsTotalCount(Long memberId);
}
