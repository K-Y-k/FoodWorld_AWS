package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminCommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private AdminMemberDTO member;
    private AdminBoardDTO board;
}
