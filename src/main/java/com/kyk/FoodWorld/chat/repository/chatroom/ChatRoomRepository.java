package com.kyk.FoodWorld.chat.repository.chatroom;

import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomRepository {

    /**
     * 채팅방 저장
     */
    ChatRoom save(ChatRoom chatRoom);

    /**
     * 해당 회원이 있는 모든 채팅방 조회
     */
    List<ChatRoom> findByMember1_IdOrMember2_Id(Long member1Id);

    /**
     * 현재 회원과 상대 회원이 있는 채팅방 조회
     */
    ChatRoom findByMember1_IdAndMember2_Id(Long member1Id, Long member2Id);

    /**
     * 현재 회원이 퇴장한 채팅방을 제외한 모든 채팅방 조회
     */
    List<ChatRoom> findNotLeaveMessageRoom(Long memberId);

    /**
     * 채팅방 id를 통한 채팅방 조회
     */
    ChatRoom findByRoomId(String roomId);

    /**
     * 채팅방 삭제
     */
    void delete(ChatRoom chatRoom);

    Page<ChatRoom> searchChatRoomByMember(String  memberSearchKeyword, Pageable pageable);
}
