package com.kyk.FoodWorld.comment.service;


import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.board.domain.entity.Board;
import com.kyk.FoodWorld.board.repository.BoardRepository;
import com.kyk.FoodWorld.comment.domain.dto.CommentUpdateDto;
import com.kyk.FoodWorld.comment.domain.dto.CommentUploadDto;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.kyk.FoodWorld.comment.repository.CommentRepository;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Override
    public Comment save(Comment comment) {
       return commentRepository.save(comment);
    }

    @Override
    public Long saveComment(Long memberId, Long boardId, CommentUploadDto dto) {
        Member memberEntity = memberRepository.findById(memberId).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 로그인 상태가 아닙니다." + memberId));
        Board boardEntity = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalArgumentException("댓글 쓰기 실패: 해당 게시글이 존재하지 않습니다." + boardId));

        Comment comment = dto.toEntity(memberEntity, boardEntity);
        commentRepository.save(comment);
        log.info("댓글 저장 성공");

        return comment.getId();
    }

    @Override
    public Page<Comment> findPageListByBoardId(Pageable pageable, Long boardId) {
        return commentRepository.findPageListByBoardId(pageable, boardId);
    }

    @Override
    public void updateComment(Long commentId, CommentUpdateDto updateParam) {
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() ->
                new IllegalArgumentException("댓글 수정 실패: 헤딩 댓글이 존재하지 않습니다." + commentId));

        findComment.updateComment(updateParam.getContent());

        log.info("댓글 수정완료");
    }

    @Override
    public int findCommentCount(Long boardId) {
        return commentRepository.findCommentCount(boardId);
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.delete(commentId);
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
