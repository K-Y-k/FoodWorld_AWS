package com.kyk.FoodWorld.menu.domain.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuRecommendUpdateForm {
    @NotEmpty(message = "카테고리를 선택해주세요!")
    private String category;

    @NotBlank(message = "식당/체인점을 입력해주세요")
    @Size(max = 60, message = "최대 10글자입니다.")
    private String franchises;

    @NotBlank(message = "메뉴 이름을 입력해주세요")
    @Size(max = 60, message = "최대 10글자입니다.")
    private String menuName;

    private MultipartFile imageFile;

}
