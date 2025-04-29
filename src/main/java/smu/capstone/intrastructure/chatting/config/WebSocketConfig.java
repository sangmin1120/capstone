package smu.capstone.intrastructure.chatting.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import smu.capstone.intrastructure.chatting.handler.StompErrorHandler;
import smu.capstone.intrastructure.chatting.handler.StompHandler;
import smu.capstone.intrastructure.chatting.util.SessionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker   //웹소켓 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static long STOMP_HEARTBEAT_TO_CLIENT = 10000;   //서버가 보내는 pong: 10초 체크
    private static long STOMP_HEARTBEAT_FROM_CLIENT = 60000; //클라이언트가 보내는 ping: 60초 체크

    private final SessionManager sessionManager;
    private final StompHandler stompHandler;
    private final StompErrorHandler stompErrorHander;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // CORS 문제 해결
        ;//.withSockJS();
        registry.setErrorHandler(stompErrorHander);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(64 * 1024)    //기본값: 64K로 설정
                .setSendTimeLimit(30 * 1000);      //sock JS에서 보내는 데 걸리는 시간 30초 설정 - 세션 종료 시킴

        registry.addDecoratorFactory(decorator -> new WebSocketHandlerDecorator(decorator) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                sessionManager.putSession(session.getId(), session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                log.info("Disconnected from websocket::{}", session.getId());
                sessionManager.removeSession(session.getId());
                super.afterConnectionClosed(session, closeStatus);
            }
        });
    }

    //STOMP pub/sub prefix 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub", "/user")   //sub 설정 -> 심플 브로커 설정 X
                .setTaskScheduler(heartBeatScheduler())
                .setHeartbeatValue(new long[]{STOMP_HEARTBEAT_TO_CLIENT,
                        STOMP_HEARTBEAT_FROM_CLIENT});
        registry.setUserDestinationPrefix("/user");//SendToUser 개별 설정
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
