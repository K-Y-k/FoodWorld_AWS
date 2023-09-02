package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminBoardDTO {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String boardType;
    private LocalDateTime createdDate;
    private int count;
    private int likeCount;
    private int commentCount;
}
