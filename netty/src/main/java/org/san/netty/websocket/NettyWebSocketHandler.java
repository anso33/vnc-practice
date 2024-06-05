package org.san.netty.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Mono;

@Component
public class NettyWebSocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyWebSocketHandler.class);

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        logger.info("New WebSocket connection established:" + session.getId() +
                    " in thread: " + Thread.currentThread().getName());

        return session.send(
            session.receive()
                .map(msg -> {
                    String payload = msg.getPayloadAsText();
                    logger.info("Received message: {} from session: {}", payload, session.getId());
                    return session.textMessage("Echo: " + payload);
                })
        ).doFinally(signal -> logger.info("WebSocket connection closed: {}", session.getId()));
    }
}