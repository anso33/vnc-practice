package com.san.rfbpractice.websocket;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PracHandShakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) {

        String ticket = request.getURI().getQuery();
        attributes.put("ticket", ticket);

        log.trace("Incoming guacamole connection" + "in thread: " + Thread.currentThread().getName());

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception exception) {
        log.trace("After handshake" + "in thread: " + Thread.currentThread().getName());
    }
}
