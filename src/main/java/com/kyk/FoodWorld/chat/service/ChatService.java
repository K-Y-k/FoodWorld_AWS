package com.kyk.FoodWorld.chat.service;


import com.kyk.FoodWorld.admin.dto.AdminChatMessageDTO;
import com.kyk.FoodWorld.chat.domain.dto.ChatMessageDto;
import com.kyk.FoodWorld.chat.domain.dto.MessageType;
import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.kyk.FoodWorld.chat.repository.chatmessage.ChatMessageRepositoryImpl;
import com.kyk.FoodWorld.chat.repository.chatroom.ChatRoomRepositoryImpl;
import com.kyk.FoodWorld.member.domain.entity.Member;
import com.kyk.FoodWorld.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepositoryImpl chatRoomRepository;
    private final ChatMessageRepositoryImpl chatMessageRepository;
    private final MemberRepository memberRepository;


    /**
     * 채팅방 서비스
     */
    /**
     * 채팅방 생성
     */
    public ChatRoom createChatRoom(Long member1Id, Long member2Id) {
        Member findMember1 = memberRepository.findById(member1Id).orElseThrow(() ->
                new IllegalArgumentException("회원 찾기 실패: " + member1Id));

        Member findMember2 = memberRepository.findById(member2Id).orElseThrow(() ->
                new IllegalArgumentException("회원 찾기 실패: " + member2Id));

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(UUID.randomUUID().toString())
                .member1(findMember1)
                .member2(findMember2)
                .build();

        chatRoomRepository.save(chatRoom);

        return chatRoom;
    }

    /**
     * 현재 유저와 관련된 모든 채팅방 조회
     */
    public List<ChatRoom> findMember1ChatRoom(Long memberId) {
        return chatRoomRepository.findByMember1_IdOrMember2_Id(memberId);
    }

    /**
     * 현재 유저와 상대 유저가 관련된 채팅방 조회
     */
    public ChatRoom findMembersChatRoom(Long member1Id, Long member2Id) {
        return chatRoomRepository.findByMember1_IdAndMember2_Id(member1Id, member2Id);
    }

    /**
     * 채팅방 id로 채팅방 조회
     */
    public ChatRoom findRoomByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    /**
     * 현재 유저가 퇴장한 채팅방은 제외한 채팅방들을 조회
     */
    public List<ChatRoom> findNotLeaveMessageRoom(Long memberId) {
        return chatRoomRepository.findNotLeaveMessageRoom(memberId);
    }

    /**
     * 채팅방 삭제
     */
    public void delete(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }

    public void deleteChatMessage(Long chatMessageId) {
        chatMessageRepository.deleteChatMessage(chatMessageId);
    }



    /**
     * 채팅 메시지 서비스
     */
    /**
     * 채팅 메시지 저장
     */
    public ChatMessage saveChatMessage(ChatMessageDto chatMessageDto) {

        ChatRoom findChatRoom = chatRoomRepository.findByRoomId(chatMessageDto.getRoomId());

        Member senderId = memberRepository.findById(chatMessageDto.getSenderId()).orElseThrow(() ->
                new IllegalArgumentException("전송 회원을 찾지 못했습니다." + chatMessageDto.getSenderId()));

        Member receiverId = memberRepository.findById(chatMessageDto.getReceiverId()).orElseThrow(() ->
                new IllegalArgumentException("받는 회원을 찾지 못했습니다." + chatMessageDto.getReceiverId()));

        ChatMessage chatMessageEntity = ChatMessage.builder()
                .chatRoom(findChatRoom)
                .messageType(chatMessageDto.getType())
                .content(chatMessageDto.getMessage())
                .sender(senderId)
                .receiver(receiverId)
                .build();

        return chatMessageRepository.save(chatMessageEntity);
    }

    /**
     * 해당 회원이 입장했던 메시지 저장되어있는지 조회
     */
    public Optional<ChatMessage> findEnterMessage(String roomId, MessageType messageType, Long senderId) {
       return chatMessageRepository.findEnterMessage(roomId, messageType, senderId);
    }

    /**
     * 대화 매시지들 조회
     */
    public List<ChatMessage> findTALKMessage(String roomId) {
        return chatMessageRepository.findTALKMessage(roomId);
    }

    /**
     * 해당 회원의 퇴장한 메시지 삭제
     */
    public void deleteLeaveMessage(String roomId, Long memberId) {
        chatMessageRepository.deleteByLeaveMessage(roomId, memberId);
    }

    public Page<ChatRoom> searchChatRoomByMember(String  memberSearchKeyword, Pageable pageable) {
        return chatRoomRepository.searchChatRoomByMember(memberSearchKeyword, pageable);
    }

    public Long findFirstCursorChatMessageId(String chatRoomId) {
        return chatMessageRepository.findFirstCursorChatMessageId(chatRoomId);
    }

    public Slice<AdminChatMessageDTO> searchBySlice(Long lastCursorChatMessageId, Boolean first, Pageable pageable, String chatRoomId) {
        return chatMessageRepository.searchBySlice(lastCursorChatMessageId, first, pageable, chatRoomId);
    }
}
