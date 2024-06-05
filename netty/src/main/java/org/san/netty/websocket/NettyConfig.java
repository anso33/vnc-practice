package org.san.netty.websocket;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

@Configuration
public class NettyConfig {

    @Bean
    public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(httpServer -> {
            int bossGroupSize = 1; // 일반적으로 1이면 충분합니다.
            int workerGroupSize = 32; // 요청 처리량을 늘리기 위해 스레드 풀 크기를 조정합니다.
            EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupSize);
            EventLoopGroup workerGroup = new NioEventLoopGroup(workerGroupSize);
            return httpServer.tcpConfiguration(tcpServer -> tcpServer
                .runOn(workerGroup)
                .wiretap(true));
        });
        return factory;
    }
}

