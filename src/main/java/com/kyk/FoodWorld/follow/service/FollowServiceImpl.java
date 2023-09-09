package com.kyk.FoodWorld.follow.service;


import com.kyk.FoodWorld.follow.domain.dto.FollowDto;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.follow.repository.FollowRepository;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService{

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Override
    public int followAndUnFollow(Long fromMemberId, Long toMemberId) {
        Optional<Follow> findFollow = followRepository.findByFromMember_IdAndToMember_Id(fromMemberId, toMemberId);

        if (findFollow.isPresent()){
            followRepository.deleteByFromMember_IdAndToMember_Id(fromMemberId, toMemberId);
            return 0;
        } else {
            Member fromMemberEntity = memberRepository.findById(fromMemberId).orElseThrow(() ->
                    new IllegalArgumentException("회원 조회 실패: " + fromMemberId));
            Member toMemberEntity = memberRepository.findById(toMemberId).orElseThrow(() ->
                    new IllegalArgumentException("회원 조회 실패: " + toMemberId));

            Follow followEntity = FollowDto.toEntity(fromMemberEntity, toMemberEntity);
            followRepository.save(followEntity);
            return 1;
        }
    }

    @Override
    public Optional<Follow>  findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId) {
        return followRepository.findByFromMember_IdAndToMember_Id(fromMemberId, toMemberId);
    }

    @Override
    public int countByToMember_Id(Long toMemberId) {
        return followRepository.countByToMember_Id(toMemberId);
    }

    @Override
    public int countByFromMember_Id(Long fromMemberId) {
        return followRepository.countByFromMember_Id(fromMemberId);
    }

    @Override
    public Long findFirstCursorFollowerId(Member member) {
        return followRepository.findFirstCursorFollowerId(member);
    }

    @Override
    public Slice<FollowDto> searchBySlice(Member member, Long lastCursorFollowerId, Boolean first, Pageable pageable) {
        return followRepository.searchBySlice(member, lastCursorFollowerId, first, pageable);
    }

    @Override
    public List<Member> recommendMember(Long currentMemberId) {
        return followRepository.recommendMember(currentMemberId);
    }
}
