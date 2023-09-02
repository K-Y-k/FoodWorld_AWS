package com.kyk.FoodWorld.comment.repository;


import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;


public interface CommentRepository {
    Comment save(Comment comment);

    Page<Comment> findPageListByBoardId(Pageable pageable, Long boardId);

    Optional<Comment> findById(Long id);
    List<Comment> findAll();

    void delete(Long commentId);

    int findCommentCount(Long boardId);

    Long findFirstCursorCommentId(String boardId, Boolean memberAdmin);
    Slice<AdminCommentDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String boardOrMemberId, Boolean memberAdmin);

}
