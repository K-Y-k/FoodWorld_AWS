package com.kyk.FoodWorld.chat.repository.chatmessage;



import com.kyk.FoodWorld.admin.dto.AdminChatMessageDTO;
import com.kyk.FoodWorld.chat.domain.dto.MessageType;
import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository {

    /**
     * 메시지 저장
     */
    ChatMessage save(ChatMessage chatMessage);


    /**
     * 해당 방에 현재 회원이 이전에 채팅 입장 메시지가 저장되어있는지 조회
     */
    Optional<ChatMessage> findEnterMessage(String roomId, MessageType messageType, Long senderId);


    /**
     * 대화 메시지 조회
     */
    List<ChatMessage> findTALKMessage(String roomId);


    /**
     * 해당 방에 현재 회원이 퇴장했던 메시지가 저장되어있으면 삭제
     */
    void deleteByLeaveMessage(String roomId, Long memberId);

    void deleteChatMessage(Long chatMessageId);


    Long findFirstCursorChatMessageId(String chatRoomId);
    Slice<AdminChatMessageDTO> searchBySlice(Long lastCursorChatMessageId, Boolean first, Pageable pageable, String chatRoomId);
}
