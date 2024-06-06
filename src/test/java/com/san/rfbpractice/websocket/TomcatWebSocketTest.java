package com.san.rfbpractice.websocket;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class TomcatWebSocketTest {

    private static final String WS_URI = "ws://localhost:8080/ws";
    private static final int CONNECTION_COUNT = 30;

    @Test
    public void testWebSocketConnections() throws InterruptedException, ExecutionException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        List<WebSocketSession> sessions = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(CONNECTION_COUNT);
        long startTime = System.nanoTime();

        ExecutorService executorService = Executors.newFixedThreadPool(CONNECTION_COUNT);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < CONNECTION_COUNT; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    WebSocketSession session = client.doHandshake(new TextWebSocketHandler() {
                        @Override
                        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                            session.sendMessage(new TextMessage("Hello"));
                            session.close();
                            sessions.add(session);
                            latch.countDown();
                        }
                    }, WS_URI).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get(10, TimeUnit.SECONDS);  // Timeout after 10 seconds if not all connections are established
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("Total time for all connections: " + (duration / 1_000_000) + " ms");

        assertTrue(latch.await(10, TimeUnit.SECONDS), "Not all WebSocket connections were established in time");

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }
}
