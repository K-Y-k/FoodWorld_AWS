package com.kyk.FoodWorld.follow.service;

import com.kyk.FoodWorld.follow.domain.dto.FollowDto;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.member.domain.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface FollowService {

    /**
     * 팔로우
     */
    int followAndUnFollow(Long fromMemberId, Long toMemberId);

    /**
     * 회원 ID와 상대 회원 둘다 있는지 조회
     */
    Optional<Follow> findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId);


    /**
     * 팔로우 수 조회
     */
    int countByToMember_Id(Long toMemberId);

    /**
     * 팔로잉 수 조회
     */
    int countByFromMember_Id(Long fromMemberId);

    /**
     * 회원을 팔로우한 회원들로 최신 팔로우 id 가져오기
     */
    Long findFirstCursorFollowerId(Member member);

    /**
     * 팔로우한 회원들만 페이징
     */
    Slice<FollowDto> searchBySlice(Member member, Long lastCursorFollowerId, Boolean first, Pageable pageable);

    /**
     * 현재 회원이 팔로우한 회원을 팔로우한 회원들과
     * 현재 회원이 팔로잉한 회원을 팔로우한 회원들을 랜덤으로 리스트로 담기
     */
    List<Member> recommendMember(Long currentMemberId);

}
