package com.kyk.FoodWorld.menu.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MenuSearchCond {
    private String selectedCategory;
    private String menuNameKeyword;
    private String franchisesKeyword;
}
