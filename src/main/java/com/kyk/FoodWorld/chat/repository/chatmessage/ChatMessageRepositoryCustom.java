package com.kyk.FoodWorld.chat.repository.chatmessage;

import com.kyk.FoodWorld.admin.dto.AdminChatMessageDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageRepositoryCustom {
    Long findFirstCursorChatMessageId(String chatRoomId);
    Slice<AdminChatMessageDTO> searchBySlice(Long lastCursorChatMessageId, Boolean first, Pageable pageable, String chatRoomId);
}
