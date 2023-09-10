package com.kyk.FoodWorld.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration // 스프링 컨테이너에 빈으로 등록
@EnableWebSocketMessageBroker // 웹 소켓 브로커를 사용하기위한 어노테이션
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // STOMP로 메시지를 처리하는 방법들을 정의한 interface

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // 클라이언트가 웹소켓 서버에 연결하는데 사용할 엔드포인트를 등록
        registry.addEndpoint("/ws-stomp") // stomp 접속 주소 url로 연결될 endPoint
                .withSockJS();                   // SockJS()로 연겷한다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");   // /pub로 시작하는 메시지가 메세지를 처리하는 메서드(message-handling methods)로 라우팅 되어야 한다고 정의
                                                              // 메시지 발행 요청으로 메시지를 보낼 때

        registry.enableSimpleBroker("/sub"); // /sub으로 시작하는 메시지가 메세지 브로커로 라우팅 되어야 한다고 정의
                                                             // 메시지 구독 요청으로 메시지를 받을 때
    }
}
