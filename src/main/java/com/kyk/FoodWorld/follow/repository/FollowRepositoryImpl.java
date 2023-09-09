package com.kyk.FoodWorld.follow.repository;

import com.kyk.FoodWorld.follow.domain.dto.FollowDto;
import com.kyk.FoodWorld.follow.domain.entity.Follow;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository{

    private final JpaFollowRepository followRepository;

    @Override
    public Follow save(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public Optional<Follow> findByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId) {
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
    public void deleteByFromMember_IdAndToMember_Id(Long fromMemberId, Long toMemberId) {
        followRepository.deleteByFromMember_IdAndToMember_Id(fromMemberId, toMemberId);
    }

    @Override
    public Long findFirstCursorFollowerId(Member member) {
        return followRepository.findFirstCursorFollowerId(member);
    }

    @Override
    public Slice<FollowDto> searchBySlice(Member member, Long lastCursorBoardId, Boolean first, Pageable pageable) {
        return followRepository.searchBySlice(member, lastCursorBoardId, first, pageable);
    }

    @Override
    public List<Member> recommendMember(Long currentMemberId) {
        return followRepository.recommendMember(currentMemberId);
    }

}
