package com.kyk.FoodWorld.member.repository;


import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * 스프링 데이터 JPA로 구현한 회원 리포지토리
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SpringDataJpaMemberRepository implements MemberRepository {
    private final JPAMemberRepository memberRepository;
    private final ProfileFileRepository profileFileRepository;

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }


    /**
     * 로그인 id로 상세조회
     */
    // 못 찾을 수 있기에 Optional로 감싸준다.
    public Optional<Member> findByLoginId(String loginId) {
       return memberRepository.findByLoginId(loginId);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }


    public void clear() {
        memberRepository.deleteAll();
    }



    @Override
    public ProfileFile saveProfile(ProfileFile profileFile) {
        return profileFileRepository.save(profileFile);
    }

    @Override
    public ProfileFile findProfileByMember(Member member) {
        return profileFileRepository.findProfileByMember(member);
    }

    @Override
    public void updateProfileImage(String originalFileName, String storedFileName, Long memberId) {
        profileFileRepository.updateProfileImage(originalFileName, storedFileName, memberId);
    }

    @Override
    public Page<Member> findPageBy(Pageable pageable) {
        return memberRepository.findPageBy(pageable);
    }

    @Override
    public Page<Member> findByNameContaining(String memberSearchKeyword, Pageable pageable) {
        return memberRepository.findByNameContaining(memberSearchKeyword, pageable);
    }

    @Override
    public void delete(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    @Override
    public int checkName(String memberName) {
        return memberRepository.checkName(memberName);
    }

    @Override
    public int checkLoginId(String memberLoginId) {
        return memberRepository.checkLoginId(memberLoginId);
    }

    @Override
    public int updateCheckName(String memberName, Long memberId) {
        return memberRepository.updateCheckName(memberName, memberId);
    }

    @Override
    public int updateCheckLoginId(String memberLoginId, Long memberId) {
        return memberRepository.updateCheckLoginId(memberLoginId, memberId);
    }

}
