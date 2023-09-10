package com.kyk.FoodWorld.chat.domain.dto;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    // 메시지  타입 : 입장, 채팅, 퇴장
    // 입장과 퇴장 ENTER과 LEAVE의 경우 입장/퇴장 이벤트 처리가 실행되고 TALK는 보낸 내용이 해당 채팅방을 구독(sub)하고 있는 모든 클라이언트에게 전달된다.

    private MessageType type; // 메시지 타입
    private String roomId;    // 방 번호
    private String sender;    // 채팅을 보낸 사람 닉네임
    private Long senderId;    // 채팅 보낸 사람 Id
    private Long receiverId;  // 채팅 받는 사람 Id
    private String senderProfile; // 채팅 보낸 사람의 프로필 사진 경로
    private String message;   // 메시지

}
