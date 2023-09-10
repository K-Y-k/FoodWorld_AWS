package com.kyk.FoodWorld.chat.repository.chatroom;

import com.kyk.FoodWorld.chat.domain.entity.ChatRoom;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kyk.FoodWorld.chat.domain.entity.QChatRoom.chatRoom;
import static org.springframework.util.StringUtils.hasText;

/**
 * 사용자 정의 인터페이스를 상속받은 사용자 정의 리포지토리
 */
@Slf4j
public class ChatRoomRepositoryCustomImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    public ChatRoomRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em); // 이렇게 JPAQueryFactory를 사용할 수는 있다.
    }


    /**
     * 현재 회원이 속해있는 채팅방 리스트 찾기
     */
    @Override
    public List<ChatRoom> findMemberChatRoom(Long memberId) {
        List<ChatRoom> findMemberChatRoom = queryFactory.selectFrom(chatRoom)
                .where(
                        chatRoom.member1.id.in(memberId)
                                .or(chatRoom.member2.id.in(memberId))
                )
                .fetch();

        return findMemberChatRoom;
    }

    /**
     * 검색한 회원의 페이징 처리된 채팅방 리스트 가져오기
     */
    @Override
    public Page<ChatRoom> searchChatRoomByMember(String memberSearchKeyword, Pageable pageable) {
        QueryResults<ChatRoom> results = queryFactory.selectDistinct(chatRoom)
                .from(chatRoom)
                .where(
                        chatRoomMemberIn(memberSearchKeyword)
                )
                .orderBy(chatRoom.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ChatRoom> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
    private BooleanExpression chatRoomMemberIn(String memberSearchKeyword) {
        BooleanExpression member1Boolean = chatRoomMember1In(memberSearchKeyword);
        BooleanExpression member2Boolean = chatRoomMember2In(memberSearchKeyword);

        if (member1Boolean == null && member2Boolean == null) {
            return null;
        } else if (member1Boolean != null && member2Boolean != null) {
            return member1Boolean.or(member2Boolean);
        } else {
            return member1Boolean != null ? member1Boolean : member2Boolean;
        }
    }
    private BooleanExpression chatRoomMember1In(String memberSearchKeyword) {
        return hasText(memberSearchKeyword) ? chatRoom.member1.name.eq(memberSearchKeyword) : null;
    }
    private BooleanExpression chatRoomMember2In(String memberSearchKeyword) {
        return hasText(memberSearchKeyword) ? chatRoom.member2.name.eq(memberSearchKeyword) : null;
    }
}
