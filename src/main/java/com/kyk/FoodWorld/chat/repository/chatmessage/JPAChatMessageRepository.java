package com.kyk.FoodWorld.chat.repository.chatmessage;


import com.kyk.FoodWorld.chat.domain.dto.MessageType;
import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JPAChatMessageRepository extends JpaRepository<ChatMessage, Long>, ChatMessageRepositoryCustom {
    @Query("select m from ChatMessage m left join m.chatRoom r " +
            "where r.roomId = :roomId and m.messageType = :messageType and m.sender.id = :senderId")
    Optional<ChatMessage> findEnterMessage(String roomId, MessageType messageType, Long senderId);

    @Modifying
    @Query("delete from ChatMessage m where m.chatRoom.id = :roomId and m.messageType = 'LEAVE' and m.sender.id = :memberId")
    void deleteByLeaveMessage(String roomId, Long memberId);

    @Query("select m from ChatMessage m left join m.chatRoom r " +
            "where r.roomId = :roomId and m.messageType = 'TALK'")
    List<ChatMessage> findTALKMessage(String roomId);

}
