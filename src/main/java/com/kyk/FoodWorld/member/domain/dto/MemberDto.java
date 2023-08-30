package com.kyk.FoodWorld.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String name;
    private String loginId;
    private String introduce;
    private int followCount;
    private int followingCount;
    private String role;
    private ProfileFileDto profileFile;
}
