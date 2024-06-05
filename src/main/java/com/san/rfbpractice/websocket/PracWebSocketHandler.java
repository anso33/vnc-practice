package com.san.rfbpractice.websocket;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PracWebSocketHandler implements WebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("New WebSocket connection: " + session.getId() + " in thread: " + Thread.currentThread().getName());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.debug("Received message: " + message.getPayload() + " in thread: " + Thread.currentThread().getName());
        // Echo the received message back to the client
        session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.info("Transport error: " + exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("WebSocket connection closed: " + session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}



