package com.san.rfbpractice.gstreamer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vnc")
public class VncController {
    private final VncService vncService;

    @GetMapping
    public void connectVnc() {
        log.info("in connectVnc controller");
        // vncService.connectVnc();
        vncService.startHlsStreaming("192.168.35.170", 5900, "anso");
    }

    @GetMapping("/streaming")
    public String startHlsStreaming() {
        vncService.startHlsStreaming("192.168.35.170", 5900, "anso");
        return "HLS streaming started.";
    }

    @GetMapping("/record")
    public String recordVncToVideo() {
        vncService.recordVncToVideo("192.168.35.170", 5900, "anso");
        return "VNC recording started.";
    }

}
