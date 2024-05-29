package com.san.rfbpractice.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
// @Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VncService {

    private Process gstProcess;

    public void connectVnc() {
        log.info("Connecting to VNC");
        VncConnection vncConnection = new VncConnection("192.168.35.170", 5900, "anso");
        vncConnection.connect();
        log.info("Connected to VNC");
    }

    public void startHlsStreaming(String vncHost, int vncPort, String vncPassword) {
        // Gst.init("", new String[] {});

        String outDir = Paths.get("out").toAbsolutePath().toString();
        String playlistLocation = Paths.get(outDir, "playlist.m3u8").toString();
        String segmentLocation = Paths.get(outDir, "segment%05d.ts").toString();

        String gstLaunchCommand = String.format(
            "gst-launch-1.0 -e -v rfbsrc host=%s port=%d password=%s ! videoconvert ! x264enc key-int-max=2 ! hlssink2 playlist-location=%s location=%s target-duration=1 max-files=5",
            vncHost, vncPort, vncPassword, playlistLocation, segmentLocation
        );

        try {
            gstProcess = Runtime.getRuntime().exec(gstLaunchCommand);

            // 파이프라인의 출력을 로그로 출력
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(gstProcess.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(gstProcess.getErrorStream()));

            // 새로운 스레드에서 출력과 오류를 읽어들임
            new Thread(() -> {
                String s;
                try {
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                String s;
                try {
                    while ((s = stdError.readLine()) != null) {
                        System.err.println(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recordVncToVideo(String vncHost, int vncPort, String vncPassword) {

        String outDir = Paths.get("out").toAbsolutePath().toString();
        String videoFilePath = Paths.get(outDir, "recorded_video.mp4").toString();

        String gstLaunchCommand = String.format(
            "gst-launch-1.0 -e -v rfbsrc host=%s port=%d password=%s ! videoconvert ! x264enc ! mp4mux ! filesink location=%s",
            vncHost, vncPort, vncPassword, videoFilePath
        );

        try {
            gstProcess = Runtime.getRuntime().exec(gstLaunchCommand);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(gstProcess.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(gstProcess.getErrorStream()));

            new Thread(() -> {
                String s;
                try {
                    while ((s = stdInput.readLine()) != null) {
                        System.out.println(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                String s;
                try {
                    while ((s = stdError.readLine()) != null) {
                        System.err.println(s);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {
        if (gstProcess != null) {
            gstProcess.destroy();
            try {
                gstProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
