package com.kyk.FoodWorld.comment.domain.dto;

import lombok.*;

import javax.validation.constraints.Size;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class CommentUpdateDto {
    @Size(max = 250, message = "최대 250글자입니다.")
    private String content;

}
