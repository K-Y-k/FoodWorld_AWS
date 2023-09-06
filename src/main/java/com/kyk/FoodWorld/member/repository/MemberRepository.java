package com.kyk.FoodWorld.member.repository;


import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


/**
 * 회원 리포지토리 기본 인터페이스
 */
public interface MemberRepository {

    Member saveMember(Member member);

    Optional<Member> findById(Long id);

    List<Member> findAll();

    Optional<Member> findByName(String name);

    Optional<Member> findByLoginId(String loginId);


    ProfileFile saveProfile(ProfileFile profileFile);

    ProfileFile findProfileByMember(Member member);

    void updateProfileImage(String originalFileName, String storedFileName, String path, Long memberId);

    Page<Member> findPageBy(Pageable pageable);
    Page<Member> findByNameContaining(String memberSearchKeyword, Pageable pageable);

    void delete(Long memberId);

    int checkName(String memberName);
    int checkLoginId(String memberLoginId);

    int updateCheckName(String memberName, Long memberId);
    int updateCheckLoginId(String memberLoginId, Long memberId);

}
