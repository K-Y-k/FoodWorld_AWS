package com.kyk.FoodWorld.member.domain.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginForm {
    @NotBlank(message = "아이디를 입력해주세요")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
