package com.kyk.FoodWorld.chat.controller;


import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.kyk.FoodWorld.chat.service.ChatService;
import com.kyk.FoodWorld.member.domain.LoginSessionConst;
import com.kyk.FoodWorld.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatService chatService;

    
    /**
     * 채팅방 기본 폼
     */
    @GetMapping("")
    public String chatRoomList(@SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER, required = false) Member loginMember,
                             Model model){

        if (loginMember != null) {
            List<ChatRoom> member1ChatRoom = chatService.findNotLeaveMessageRoom(loginMember.getId());

            if (!member1ChatRoom.isEmpty()) {
                model.addAttribute("member1ChatRoom", member1ChatRoom);
                model.addAttribute("localDateTime", LocalDateTime.now());
            }
        }

        return "chat/chat";
    }


    /**
     * 처음 상대방과 채팅하기 버튼 클릭시 채팅방 매칭
     */
    @GetMapping("/matchingRoom/{memberId}")
    public String goChatRoom(@PathVariable Long memberId,
                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER) Member loginMember,
                             RedirectAttributes redirectAttributes,
                             Model model){

        // 현재 회원과 채팅을 원하는 상대 회원의 채팅방을 찾고
        ChatRoom findMembersChatRoom1 = chatService.findMembersChatRoom(loginMember.getId(), memberId);
        ChatRoom findMembersChatRoom2 = chatService.findMembersChatRoom(memberId, loginMember.getId());

        if (findMembersChatRoom1 == null && findMembersChatRoom2 == null) { // 채팅방이 없으면 새로 만든 후 전체 채팅방 조회
            chatService.createChatRoom(loginMember.getId(), memberId);

            List<ChatRoom> member1ChatRoom = chatService.findMember1ChatRoom(loginMember.getId());
            redirectAttributes.addFlashAttribute("member1ChatRoom", member1ChatRoom);
            redirectAttributes.addFlashAttribute("localDateTime", LocalDateTime.now());
        } else { // 기존에 있으면 전체 채팅방 조회
            // 만약 기존에 채팅을 하다가 퇴장했을 경우를 위해 양쪽 모두 퇴장 메시지를 먼저 지움
            if (findMembersChatRoom1 != null) {
                chatService.deleteLeaveMessage(findMembersChatRoom1.getRoomId(), loginMember.getId());
                chatService.deleteLeaveMessage(findMembersChatRoom1.getRoomId(), memberId);
            } else {
                chatService.deleteLeaveMessage(findMembersChatRoom2.getRoomId(), loginMember.getId());
                chatService.deleteLeaveMessage(findMembersChatRoom2.getRoomId(), memberId);
            }

            List<ChatRoom> member1ChatRoom = chatService.findMember1ChatRoom(loginMember.getId());
            redirectAttributes.addFlashAttribute("member1ChatRoom", member1ChatRoom);
            redirectAttributes.addFlashAttribute("localDateTime", LocalDateTime.now());
        }
        return "redirect:/chat";
    }


    /**
     * 클릭한 채팅방 조회
     */
    @GetMapping("/room")
    public String goChatRoom(@RequestParam String roomId,
                             @SessionAttribute(name = LoginSessionConst.LOGIN_MEMBER) Member loginMember,
                             Model model){
        // 클릭한 방을 조회
        ChatRoom targetChatRoom = chatService.findRoomByRoomId(roomId);
        model.addAttribute("targetChatRoom", targetChatRoom);

        // 클릭한 방의 대화 메시지 가져오기
        List<ChatMessage> chatMessages = chatService.findTALKMessage(roomId);
        model.addAttribute("chatMessages", chatMessages);

        // 현재 회원의 전체 채팅방 리스트
        List<ChatRoom> member1ChatRoom = chatService.findMember1ChatRoom(loginMember.getId());
        model.addAttribute("member1ChatRoom", member1ChatRoom);

        // 등록한 날이 오늘 날짜이면 시/분까지만 나타나게 조건을 설정하기 위해서 현재 시간을 객체로 담아 보낸 것
        model.addAttribute("localDateTime", LocalDateTime.now());

        return "chat/chat";
    }

}
