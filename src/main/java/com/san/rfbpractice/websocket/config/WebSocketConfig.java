package com.san.rfbpractice.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.san.rfbpractice.websocket.PracHandShakeInterceptor;
import com.san.rfbpractice.websocket.PracWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final PracWebSocketHandler webSocketHandler;
    private final PracHandShakeInterceptor webSocketHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*")
            .addInterceptors(webSocketHandshakeInterceptor);
    }

}
