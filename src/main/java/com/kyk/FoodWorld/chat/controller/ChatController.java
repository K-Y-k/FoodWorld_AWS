package com.kyk.FoodWorld.chat.controller;


import com.kyk.FoodWorld.chat.domain.dto.ChatMessageDto;
import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.kyk.FoodWorld.chat.service.ChatService;
import com.kyk.FoodWorld.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Optional;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    // convertAndSend를 사용하기 위해서 선언
    // convertAndSend는 객체를 인자로 넘겨주면 자동으로 Message 객체로 변환 후 도착지로 전송한다.
    private final SimpMessageSendingOperations template;

    private final ChatService chatService;

    private final MemberServiceImpl memberService;


    /**
     * 입장 시
     * : MessageMapping을 통해 웹 소켓으로 들어오는 메시지를 발신 처리한다.
     *   이때 클라이언트에서는 /pub/chat/message로 요청하게 되고 이것을 controller가 받아서 처리한다.
     *   처리가 완료되면 /sub/chat/room/roomId로 메시지가 전송된다.
     */
    @MessageMapping("/chat/enterUser")
    public void enterUser(@Payload ChatMessageDto messageDto) {
        // 이전에 이 채팅방을 입장한 적이 있는지 확인
        Optional<ChatMessage> isEnter = chatService.findEnterMessage(messageDto.getRoomId(), messageDto.getType(), messageDto.getSenderId());

        if (isEnter.isEmpty()) { // 입장한 적이 없으면 입장 메시지 전송
            messageDto.setMessage(messageDto.getSender() + " 입장");
            template.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
            chatService.saveChatMessage(messageDto);
        }
    }


    /**
     * 채팅 전송시
     * : 해당 유저 메세지 전송
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessageDto messageDto) {
        log.info("채팅 전송한 메시지 = {}", messageDto);

        // 프로필 사진 갱신
        String profilePath = memberService.findProfileLocation(messageDto.getSenderId());
        messageDto.setSenderProfile(profilePath);

        template.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
        chatService.saveChatMessage(messageDto);


        // 만약 회원이 퇴장을 했는데 기존 채팅방에 머문 상대가 다시 메시지를 보낸 경우이면
        // 다시 채팅방이 생성하도록 퇴장 메시지 삭제
        ChatRoom findMembersChatRoom1 = chatService.findMembersChatRoom(messageDto.getSenderId(), messageDto.getReceiverId());
        ChatRoom findMembersChatRoom2 = chatService.findMembersChatRoom(messageDto.getReceiverId(), messageDto.getSenderId());

        if (findMembersChatRoom1 != null) {
            chatService.deleteLeaveMessage(findMembersChatRoom1.getRoomId(), messageDto.getReceiverId());
        } else {
            chatService.deleteLeaveMessage(findMembersChatRoom2.getRoomId(), messageDto.getReceiverId());
        }
    }


    /**
     * 퇴장 시
     * : 입장할때와 동일한 메커니즘
     */
    @MessageMapping("/chat/leaveUser")
    public void leaveUser(@Payload ChatMessageDto messageDto) {
        // 상대가 퇴장한 메시지가 있는지 조회
        Optional<ChatMessage> youIsLeave = chatService.findEnterMessage(messageDto.getRoomId(), messageDto.getType(), messageDto.getReceiverId());

        log.info("youIsLeave={}", youIsLeave.isPresent());

        // 상대방도 이미 퇴장한 상태라면 채팅방을 아예 지움
        if (youIsLeave.isPresent()) {
            // 채팅방 삭제, 먼저 삭제해야 리다이렉트할 때 채팅방이 남지 않는다!
            ChatRoom findChatRoom = chatService.findRoomByRoomId(messageDto.getRoomId());
            chatService.delete(findChatRoom);
            
            // 퇴장 메시지 전송
            messageDto.setMessage("채팅방 삭제");
            template.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
        }
        else { // 상대방이 퇴장하지 않은 상태이면
            // 퇴장 메시지 전송
            messageDto.setMessage(messageDto.getSender() + " 퇴장하였습니다");
            template.convertAndSend("/sub/chat/room/" + messageDto.getRoomId(), messageDto);
            chatService.saveChatMessage(messageDto);
        }
    }


    // 회원 퇴장 시 퇴장 메시지 저장 및 EventListener을 통해서 퇴장 알림
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        log.info("DisConnEvent {}", event);
//
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//
//        // 채팅방 유저 리스트에서 퇴장한 회원 조회 및 삭제
//
//
//        // 퇴장 알림
//        ChatMessageDto chat = ChatMessageDto.builder()
//                .type(MessageType.LEAVE)
//                .sender(username)
//                .message(username + " 퇴장!!")
//                .build();
//
//        template.convertAndSend("/sub/chat/room/" + roomId, chat);
//    }


}
