package com.kyk.FoodWorld.like.service;

public interface LikeService {

    /**
     * 좋아요 저장
     */
    int saveLike(Long memberId, Long boardId);

    /**
     * 게시글의 id에 맞는 좋아요 총 개수 파악
     */
    int countByBoard_Id(Long boardId);

    /**
     * 해당 게시글의 좋아요를 모두 삭제
     */
    void deleteByBoard_Id(Long boardId);

}
