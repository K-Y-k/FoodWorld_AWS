package com.kyk.FoodWorld.menu.domain.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MenuRecommendDto {
    private Long id;

    private String category;

    private String franchises;

    private String menuName;

}
