package com.kyk.FoodWorld.comment.repository;

import com.kyk.FoodWorld.admin.dto.AdminBoardDTO;
import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import com.kyk.FoodWorld.admin.dto.AdminMemberDTO;
import com.kyk.FoodWorld.admin.dto.AdminProfileFileDTO;
import com.kyk.FoodWorld.comment.domain.entity.Comment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;


import static com.kyk.FoodWorld.board.domain.entity.QBoard.board;
import static com.kyk.FoodWorld.comment.domain.entity.QComment.comment;
import static com.kyk.FoodWorld.member.domain.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스를 상속받은 사용자 정의 리포지토리
 */
@Slf4j
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public CommentRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    /**
     * 처음에는 최근 Id로 직접 받아오기 위해 필요한 메서드
     */
    @Override
    public Long findFirstCursorCommentId(String boardId, Boolean memberAdmin) {
        Comment findComment = queryFactory.selectFrom(comment)
                .where(
                        boardOrMemberIdEq(boardId, memberAdmin)
                )
                .limit(1)
                .orderBy(comment.id.desc())
                .fetchOne();

        if (findComment != null) {
            return findComment.getId();
        }
        else {
            return 0L;
        }
    }

    /**
     * 크기를 제한한 리스트와 다음 페이지의 여부를 같이 반환하는 Slice 페이징
     */
    @Override
    public Slice<AdminCommentDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String boardOrMemberId, Boolean memberAdmin) {

        List<Comment> comments = queryFactory.selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.board, board).fetchJoin()
                .where(
                        ltCommentId(lastCursorId, first),
                        boardOrMemberIdEq(boardOrMemberId, memberAdmin)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(comment.id.desc())
                .fetch();

        log.info("리스트 개수={}", comments.size());

        List<AdminCommentDTO> commentDTOList = comments.stream()
                .map(m -> new AdminCommentDTO(m.getId(), m.getContent(), m.getCreatedDate(),
                        new AdminMemberDTO(m.getMember().getId(), m.getMember().getName(),
                                new AdminProfileFileDTO(m.getMember().getProfileFile().getPath())),
                        new AdminBoardDTO(m.getBoard().getId(), m.getBoard().getTitle(), m.getBoard().getContent(), m.getBoard().getWriter(), m.getBoard().getBoardType(), m.getCreatedDate(), m.getBoard().getCount(), m.getBoard().getLikeCount(), m.getBoard().getCommentCount())))
                .collect(Collectors.toList());

        return checkLastPage(pageable, commentDTOList);
    }
    // BooleanExpression으로 하면 조합 가능해진다.
    private BooleanExpression boardOrMemberIdEq(String boardId, Boolean memberAdmin) {
        if (memberAdmin) {
            return hasText(boardId) ? comment.member.id.eq(Long.valueOf(boardId)) : null;
        }
        else {
            return hasText(boardId) ? comment.board.id.eq(Long.valueOf(boardId)) : null;
        }
    }

    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltCommentId(Long lastCursorBoardId, Boolean first) {
        if (lastCursorBoardId == null) {
            return null;
        }
        else if (first) {
            return comment.id.loe(lastCursorBoardId);
        }
        else {
            return comment.id.lt(lastCursorBoardId);
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<AdminCommentDTO> checkLastPage(Pageable pageable, List<AdminCommentDTO> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

}
