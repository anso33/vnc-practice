package com.san.rfbpractice.websocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/test")
    public void test() {
        log.trace("Test controller" + "in thread: " + Thread.currentThread().getName());
    }
}
