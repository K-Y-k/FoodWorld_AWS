package com.kyk.FoodWorld.member.service;


import com.kyk.FoodWorld.member.domain.dto.JoinForm;
import com.kyk.FoodWorld.member.domain.dto.MemberDto;
import com.kyk.FoodWorld.member.domain.dto.UpdateForm;
import com.kyk.FoodWorld.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface MemberService {

    /**
     * 회원 저장
     */
    Long join(JoinForm form);

    /**
     * 회원 로그인
     */
    Member login(String loginId, String password);


    /**
     * 회원 id로 조회
     */
    MemberDto findMemberDtoById(Long memberId);

    /**
     * 회원 모두 조회
     */
    List<Member> findAll();

    Long changeProfile(Long memberId, UpdateForm form) throws IOException;

    Page<Member> findPageBy(Pageable pageable);
    Page<Member> findByNameContaining(String memberSearchKeyword, Pageable pageable);

    void delete(Long memberId);

    int checkName(String memberName);
    int checkLoginId(String memberLoginId);
    int updateCheckName(String memberName, Long memberId);
    int updateCheckLoginId(String memberLoginId, Long memberId);


}
