package com.kyk.FoodWorld.member.domain.dto;

import com.kyk.FoodWorld.member.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String loginId;
    private String password;
    private String introduce;
    private int followCount;
    private int followingCount;
    private String role;
    private ProfileFileDto profileFile;


    public MemberDto(Long id, String name, String loginId, String password, String introduce, ProfileFileDto profileFile) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.introduce = introduce;
        this.profileFile = profileFile;
    }

    public MemberDto(Long id, String name, String loginId, String introduce, int followCount, int followingCount, String role, ProfileFileDto profileFile) {
        this.id = id;
        this.name = name;
        this.loginId = loginId;
        this.introduce = introduce;
        this.followCount = followCount;
        this.followingCount = followingCount;
        this.role = role;
        this.profileFile = profileFile;
    }
}
