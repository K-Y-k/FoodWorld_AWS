package com.kyk.FoodWorld.chat.repository.chatroom;

import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepository{

    private final JPAChatRoomRepository chatRoomRepository;

    @Override
    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }


    @Override
    public List<ChatRoom> findByMember1_IdOrMember2_Id(Long member1Id) {
        return chatRoomRepository.findMemberChatRoom(member1Id);
    }

    @Override
    public ChatRoom findByMember1_IdAndMember2_Id(Long member1Id, Long member2Id) {
        return chatRoomRepository.findByMember1_IdAndMember2_Id(member1Id, member2Id);
    }

    @Override
    public List<ChatRoom> findNotLeaveMessageRoom(Long memberId) {
        return chatRoomRepository.findNotLeaveMessageRoom(memberId);
    }

    @Override
    public ChatRoom findByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    @Override
    public void delete(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }

    @Override
    public Page<ChatRoom> searchChatRoomByMember(String memberSearchKeyword, Pageable pageable) {
        return chatRoomRepository.searchChatRoomByMember(memberSearchKeyword, pageable);
    }
}
