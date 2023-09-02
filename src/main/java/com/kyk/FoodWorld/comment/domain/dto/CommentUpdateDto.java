package com.kyk.FoodWorld.comment.domain.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class CommentUpdateDto {
    private String content;

}
