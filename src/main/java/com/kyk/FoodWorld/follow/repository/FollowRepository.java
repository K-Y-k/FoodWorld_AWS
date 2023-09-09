package com.kyk.FoodWorld.follow.repository;


import com.kyk.FoodWorld.follow.domain.dto.FollowDto;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.member.domain.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {

    /**
     * 팔로우
     */
    Follow save(Follow follow);

    /**
     * 회원 ID와 게시글 ID 둘다 있는지 조회
     */
    Optional<Follow> findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);


    /**
     * 팔로워 수 조회
     */
    int countByToMember_Id(Long toMemberId);

    /**
     * 팔로잉 수 조회
     */
    int countByFromMember_Id(Long fromMemberId);


    /**
     * 팔로우 취소
     */
    void deleteByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);


    Long findFirstCursorFollowerId(Member member);
    Slice<FollowDto> searchBySlice(Member member, Long lastCursorBoardId, Boolean first, Pageable pageable);
    List<Member> recommendMember(Long currentMemberId);
}
