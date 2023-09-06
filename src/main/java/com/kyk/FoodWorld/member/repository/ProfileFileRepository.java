package com.kyk.FoodWorld.member.repository;


import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.ProfileFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 파일 관련 스프링 데이터 JPA 리포지토리
 */
public interface ProfileFileRepository extends JpaRepository<ProfileFile, Long> {

    /**
     * 현재 회원의 프로필 사진 엔티티 조회
     */
    ProfileFile findProfileByMember(Member member);

    /**
     * 프로필 사진 엔티티의 필드 내용을 변경
     */
    @Modifying
    @Query("update ProfileFile p set p.originalFileName = :originalFileName, p.storedFileName = :storedFileName, p.path = :path where p.member.id = :memberId")
    void updateProfileImage(String originalFileName, String storedFileName, String path, Long memberId);

}
