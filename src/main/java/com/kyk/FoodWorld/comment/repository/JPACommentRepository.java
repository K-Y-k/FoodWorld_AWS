package com.kyk.FoodWorld.comment.repository;

import com.kyk.FoodWorld.comment.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface JPACommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    /**
     * 댓글 개수 조회
     */
    @Query("select count(*) from Comment c where c.board.id = :boardId")
    int findCommentCount(Long boardId);


    /**
     * 댓글 페이징
     */
    Page<Comment> findPageListByBoardId(Pageable pageable, Long boardId);


    /**
     * 댓글 삭제
     */
    @Modifying
    @Query("delete Comment c where c.board.id = :boardId and c.member.id = :memberId")
    void deleteComment(Long boardId, Long memberId);
}
