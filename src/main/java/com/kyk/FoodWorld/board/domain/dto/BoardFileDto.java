package com.kyk.FoodWorld.board.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BoardFileDto {
    private String originalFileName;
    private String storedFileName;
    private String attachedType;
}
