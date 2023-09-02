package com.kyk.FoodWorld.comment.repository;

import com.kyk.FoodWorld.admin.dto.AdminCommentDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * QueryDsl을 구현하기 위한 사용자 정의 인터페이스
 */
public interface CommentRepositoryCustom {
    Long findFirstCursorCommentId(String boardId, Boolean memberAdmin);
    Slice<AdminCommentDTO> searchBySlice(Long lastCursorId, Boolean first, Pageable pageable, String boardOrMemberId, Boolean memberAdmin);
}
