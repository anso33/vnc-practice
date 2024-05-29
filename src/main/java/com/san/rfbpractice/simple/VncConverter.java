package com.san.rfbpractice.simple;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VncConverter {
    public void convertVncToHls(String vncHost, int vncPort, String vncPassword, String playlistLocation,
        String segmentLocation) {
        // GStreamer CLI 명령어 생성
        String gstLaunchCommand = String.format(
            "gst-launch-1.0 -e -v rfbsrc host=%s port=%d password=%s ! videoconvert ! x264enc key-int-max=2 ! hlssink2 playlist-location=%s location=%s target-duration=1 max-files=5",
            vncHost, vncPort, vncPassword, playlistLocation, segmentLocation
        );

        try {
            // GStreamer 파이프라인 실행
            log.info("gstLaunchCommand: {}", gstLaunchCommand);
            Process gstProcess = Runtime.getRuntime().exec(gstLaunchCommand);
            // 파이프라인 실행 후 작업 수행
            log.info("gstProcess: {}", gstProcess);
            // ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void convertVncToHls(String vncHost, int vncPort, String vncPassword, String playlistLocation, String segmentLocation) {
    //     // // GStreamer 초기화
    //     // Gst.init("VncConverter", new String[]{});
    //     //
    //     // // HLS 스트리밍을 위한 HlsSink 엘리먼트 생성
    //     // HlsSink hlssink = (HlsSink) ElementFactory.make("hlssink", "hlssink");
    //     // hlssink.set("playlist-location", playlistLocation);
    //     // hlssink.set("location", segmentLocation);
    //     // hlssink.set("target-duration", 1);
    //     // hlssink.set("max-files", 5);
    //     //
    //     // // VNC 서버에 연결하는 rfbsrc 엘리먼트 생성
    //     // String rfbSrcString = String.format("rfbsrc host=%s port=%d password=%s", vncHost, vncPort, vncPassword);
    //     // Pipeline pipeline = Pipeline.launch(rfbSrcString + " ! videoconvert ! x264enc ! " + hlssink.toString());
    //     //
    //     // // 파이프라인 실행
    //     // pipeline.play();
    //     // HLS 스트리밍을 위한 파이프라인 생성
    //     String hlsPipelineString = String.format("%s ! videoconvert ! x264enc ! hlsmux ! filesink location=%s", rfbSrcString, playlistLocation);
    //     Pipeline hlsPipeline = Pipeline.launch(hlsPipelineString);
    // }

    // public void recordVncToVideo(String vncHost, int vncPort, String vncPassword, String outputFile) {
    //     // GStreamer 초기화
    //     Gst.init("VncConverter", new String[]{});
    //
    //     // 비디오를 파일로 녹화하기 위한 splitmuxsink 엘리먼트 생성
    //     SplitMuxSink splitmuxsink = (SplitMuxSink) ElementFactory.make("splitmuxsink", "splitmuxsink");
    //     splitmuxsink.set("location", outputFile);
    //     splitmuxsink.set("muxer", "mp4mux");
    //     splitmuxsink.set("max-size-time", 10 * Gst.SECOND); // 10초마다 세그먼트 분할
    //
    //     // VNC 서버에 연결하는 rfbsrc 엘리먼트 생성
    //     String rfbSrcString = String.format("rfbsrc host=%s port=%d password=%s", vncHost, vncPort, vncPassword);
    //     Pipeline pipeline = Pipeline.launch(rfbSrcString + " ! videoconvert ! x264enc ! " + splitmuxsink.toString());
    //
    //     // 파이프라인 실행
    //     pipeline.play();
    // }
}
