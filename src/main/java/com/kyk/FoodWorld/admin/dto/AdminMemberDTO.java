package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminMemberDTO {
    private Long id;
    private String name;
    private AdminProfileFileDTO profileFile;
}
