package org.san.netty.websocket;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

import reactor.core.publisher.Mono;

class NettyWebSocketTest {

    Logger logger = LoggerFactory.getLogger(NettyWebSocketTest.class);

    @Test
    public void testWebSocketConnections() throws InterruptedException, ExecutionException {
        ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();
        URI url = URI.create("ws://localhost:8080/ws");

        ExecutorService executorService = Executors.newFixedThreadPool(30);
        List<Future<Void>> futures = new ArrayList<>();

        long startTime = System.nanoTime(); // 시작 시간 기록

        for (int i = 0; i < 30; i++) {
            futures.add(executorService.submit(() -> {
                client.execute(url, session ->
                    session.send(Mono.just(session.textMessage("Hello")))
                        .thenMany(session.receive()
                            .take(1)
                            .map(msg -> {
                                logger.info(
                                    "sessionId" + session.getId() + " Received: " + msg.getPayloadAsText());
                                return msg;
                            }))
                        .then()
                ).block(Duration.ofSeconds(10));
                return null;
            }));
        }

        // Wait for all futures to complete
        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        long endTime = System.nanoTime(); // 종료 시간 기록
        long duration = endTime - startTime; // 총 소요 시간 계산

        logger.info("Total time for all connections: " + (duration / 1_000_000) + " ms");
    }
}
