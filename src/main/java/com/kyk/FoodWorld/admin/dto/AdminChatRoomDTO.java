package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminChatRoomDTO {
    private String roomId;
    private AdminMemberDTO member1;
    private AdminMemberDTO member2;
}
