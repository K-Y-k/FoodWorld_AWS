package com.kyk.FoodWorld.member.domain.dto;

import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.domain.entity.Role;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class JoinForm {

    private Long id;

    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 2, max = 7, message = "최소 2글자, 최대 7글자입니다.")
    private String name;

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 3, max = 10, message = "최소 3글자, 최대 10글자입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 3, max = 10, message = "최소 3글자, 최대 10글자입니다.")
    private String password;


    // 생성자 레벨에 @Builder을 작성해주게되면
    // 해당 필드 값들에 대해서는 Builder class를 자동으로 생성해준다.
    @Builder
    public JoinForm(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
    }

    // 엔티티에 @setter를 사용하지 않기 위해 dto에서 엔티티로 변환해주는 메서드 적용
    public Member toEntity() {
        return Member.builder()
                .name(name)
                .loginId(loginId)
                .password(password)
                .role(Role.CUSTOMER)
                .build();
    }

}
