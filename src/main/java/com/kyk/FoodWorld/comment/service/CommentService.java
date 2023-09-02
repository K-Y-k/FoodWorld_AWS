package com.kyk.FoodWorld.comment.service;


import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.comment.domain.dto.CommentUpdateDto;
import com.kyk.FoodWorld.comment.domain.dto.CommentUploadDto;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface CommentService {

    Comment save(Comment comment);
    Long saveComment(Long memberId, Long boardId, CommentUploadDto dto);

    Page<Comment> findPageListByBoardId(Pageable pageable, Long boardId);

    void updateComment(Long commentId, CommentUpdateDto updateParam);

    int findCommentCount(Long boardId);

    void delete(Long commentId);

    Long findFirstCursorCommentId(String boardId, Boolean memberAdmin);
    Slice<AdminCommentDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String boardOrMemberId, Boolean memberAdmin);

}
