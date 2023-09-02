package com.kyk.FoodWorld.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AdminChatMessageDTO {
    private Long id;
    private LocalDateTime createdDate;
    private String content;
    private String sender;
    private String senderProfile;
    private AdminChatRoomDTO chatRoom;
}
