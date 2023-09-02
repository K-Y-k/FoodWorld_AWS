package com.kyk.FoodWorld.like.repository;


import com.kyk.FoodWorld.like.domain.entity.Like;

import java.util.Optional;

public interface LikeRepository {

    /**
     * 좋아요 엔티티에 저장
     */
    Like save(Like like);


    /**
     * 회원 ID와 게시글 ID 둘다 있는지 조회
     */
    Optional<Like> findByMember_IdAndBoard_Id(Long memberId, Long boardId);


    /**
     * 해당 게시글의 좋아요 수 조회
     */
    int countByBoard_Id(Long boardId);


    /**
     * 좋아요 엔티티에서 삭제
     */
    void delete(Long likeId);


    /**
     * 해당 게시글의 좋아요를 모두 삭제
     */
    void deleteByBoardId(Long boardId);

}
