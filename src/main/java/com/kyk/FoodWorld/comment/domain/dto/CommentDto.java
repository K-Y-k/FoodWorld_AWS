package com.kyk.FoodWorld.comment.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private String writer;
    private Long boardId;

}
