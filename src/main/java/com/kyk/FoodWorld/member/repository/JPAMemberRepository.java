package com.kyk.FoodWorld.member.repository;


import com.kyk.FoodWorld.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * SpringDataJpa를 이용한 리포지토리 인터페이스
 *  구현체 없이 기본적인 CRUD 기능 및 공통 기술 제공
 *  메서드 이름과 관련된 기능 파악해서 자동 제공
 */
public interface JPAMemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
    Optional<Member> findByLoginId(String loginId);
    Page<Member> findPageBy(Pageable pageable);
    Page<Member> findByNameContaining(String memberSearchKeyword, Pageable pageable);

    @Query("select count(m) from Member m " +
            "where m.name = :memberName")
    int checkName(String memberName);

    @Query("select count(m) from Member m " +
            "where m.loginId = :memberLoginId")
    int checkLoginId(String memberLoginId);

    @Query("select count(m) from Member m " +
            "where m.id != :memberId and m.name = :memberName")
    int updateCheckName(String memberName, Long memberId);

    @Query("select count(m) from Member m " +
            "where m.id != :memberId and m.loginId = :memberLoginId")
    int updateCheckLoginId(String memberLoginId, Long memberId);

}
