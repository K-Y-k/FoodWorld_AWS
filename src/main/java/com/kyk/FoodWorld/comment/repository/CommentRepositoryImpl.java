package com.kyk.FoodWorld.comment.repository;


import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository{

    private final JPACommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
       return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> findPageListByBoardId(Pageable pageable, Long boardId) {
        return commentRepository.findPageListByBoardId(pageable, boardId);
    }


    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public int findCommentCount(Long boardId) {
        return commentRepository.findCommentCount(boardId);
    }

    @Override
    public Long findFirstCursorCommentId(String boardId, Boolean memberAdmin) {
        return commentRepository.findFirstCursorCommentId(boardId, memberAdmin);
    }

    @Override
    public Slice<AdminCommentDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String boardOrMemberId, Boolean memberAdmin) {
        return commentRepository.searchBySlice(lastCursorId, first, pageable, boardOrMemberId, memberAdmin);
    }


}
