package com.kyk.FoodWorld.chat.repository.chatmessage;

import com.kyk.FoodWorld.admin.dto.AdminChatMessageDTO;
import com.kyk.FoodWorld.admin.dto.AdminChatRoomDTO;
import com.kyk.FoodWorld.admin.dto.AdminMemberDTO;
import com.kyk.FoodWorld.admin.dto.AdminProfileFileDTO;
import com.kyk.FoodWorld.chat.domain.entity.ChatMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;


import static com.kyk.FoodWorld.chat.domain.entity.QChatMessage.chatMessage;
import static com.kyk.FoodWorld.chat.domain.entity.QChatRoom.chatRoom;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스를 상속받은 사용자 정의 리포지토리
 */
@Slf4j
public class ChatMessageRepositoryCustomImpl implements ChatMessageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public ChatMessageRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em); // 이렇게 JPAQueryFactory를 사용할 수는 있다.
    }


    /**
     * 처음에는 최근 Id로 직접 받아오기 위해 필요한 메서드
     */
    @Override
    public Long findFirstCursorChatMessageId(String chatRoomId) {
        ChatMessage findChatMessage = queryFactory.selectFrom(chatMessage)
                .where(
                        chatMessage.chatRoom.roomId.eq(chatRoomId)
                )
                .limit(1)
                .orderBy(chatMessage.id.desc())
                .fetchOne();

        if (findChatMessage != null) {
            return findChatMessage.getId();
        }
        else {
            return 0L;
        }
    }

    /**
     * 크기를 제한한 리스트와 다음 페이지의 여부를 같이 반환하는 Slice 페이징
     */
    @Override
    public Slice<AdminChatMessageDTO> searchBySlice(Long lastCursorChatMessageId, Boolean first, Pageable pageable, String chatRoomId) {

        List<ChatMessage> chatMessages = queryFactory.selectFrom(chatMessage)
                .leftJoin(chatMessage.chatRoom, chatRoom).fetchJoin()
                .where(
                        ltChatMessageId(lastCursorChatMessageId, first),
                        chatRoomIdEq(chatRoomId)
                )
                .limit(pageable.getPageSize() + 1)
                .orderBy(chatMessage.id.desc())
                .fetch();

        log.info("리스트 개수={}", chatMessages.size());

        List<AdminChatMessageDTO> chatMessageDTOList = chatMessages.stream()
                .map(m -> new AdminChatMessageDTO(m.getId(), m.getCreatedDate(), m.getContent(), m.getSender(), m.getSenderProfile(),
                        new AdminChatRoomDTO(m.getChatRoom().getRoomId(),
                                new AdminMemberDTO(m.getChatRoom().getMember1().getId(), m.getChatRoom().getMember1().getName(),
                                        new AdminProfileFileDTO(m.getChatRoom().getMember1().getProfileFile().getPath())),
                                new AdminMemberDTO(m.getChatRoom().getMember2().getId(), m.getChatRoom().getMember2().getName(),
                                        new AdminProfileFileDTO(m.getChatRoom().getMember2().getProfileFile().getPath())))))
                .collect(Collectors.toList());

        return checkLastPage(pageable, chatMessageDTOList);
    }
    // BooleanExpression으로 하면 조합 가능해진다.
    private BooleanExpression chatRoomIdEq(String chatRoomId) {
        return hasText(chatRoomId) ? chatMessage.chatRoom.roomId.eq(chatRoomId) : null;
    }

    // no-offset 방식 처리하는 메서드
    private BooleanExpression ltChatMessageId(Long lastCursorChatMessageId, Boolean first) {
        if (lastCursorChatMessageId == null) {
            return null;
        }
        else if (first) {
            return chatMessage.id.loe(lastCursorChatMessageId);
        }
        else {
            return chatMessage.id.lt(lastCursorChatMessageId);
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private Slice<AdminChatMessageDTO> checkLastPage(Pageable pageable, List<AdminChatMessageDTO> results) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
