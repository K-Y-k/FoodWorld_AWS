package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminMenuRecommendDTO {
    private Long id;
    private String category;
    private String franchises;
    private String menuName;
    private LocalDateTime createdDate;
}
