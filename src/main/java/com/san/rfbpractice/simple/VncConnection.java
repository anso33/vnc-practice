package com.san.rfbpractice.simple;

import java.net.Socket;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VncConnection {
    private final String host;
    private final int port;
    private final String password;
    private Socket socket;

    public VncConnection(String host, int port, String password) {
        this.host = host;
        this.port = port;
        this.password = password;
    }

    public void connect() {
        VncConverter vncConverter = new VncConverter();

        String outDir = Paths.get("out").toAbsolutePath().toString();
        String playlistLocation = Paths.get(outDir, "playlist.m3u8").toString();
        String segmentLocation = Paths.get(outDir, "segment%05d.ts").toString();
        log.info("playlistLocation: {}", playlistLocation);
        vncConverter.convertVncToHls(host, port, password, playlistLocation, segmentLocation);
    }
}
