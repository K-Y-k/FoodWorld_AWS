package com.kyk.FoodWorld.chat.repository.chatroom;

import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomRepositoryCustom {

    List<ChatRoom> findMemberChatRoom(Long memberId);
    Page<ChatRoom> searchChatRoomByMember(String  memberSearchKeyword, Pageable pageable);
}
