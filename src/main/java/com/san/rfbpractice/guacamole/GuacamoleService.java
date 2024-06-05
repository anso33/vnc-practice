package com.san.rfbpractice.guacamole;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.guacamole.GuacamoleConnectionClosedException;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.io.GuacamoleReader;
import org.apache.guacamole.io.GuacamoleWriter;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.InetGuacamoleSocket;
import org.apache.guacamole.net.SimpleGuacamoleTunnel;
import org.apache.guacamole.protocol.ConfiguredGuacamoleSocket;
import org.apache.guacamole.protocol.GuacamoleConfiguration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GuacamoleService {
    private static final int BUFFER_SIZE = 8192;

    private final HashMap<Long, String> idMap = new HashMap<>(240);
    private final ConcurrentHashMap<String, String> connections = new ConcurrentHashMap<>(240);

    // public GuacamoleService() {
    //     guacamoleConfig = new GuacamoleConfiguration();
    //     guacamoleConfig.setProtocol("vnc");
    //     guacamoleConfig.setParameter("hostname", "your-host-ip");
    //     guacamoleConfig.setParameter("port", "5900");
    // }

    private GuacamoleTunnel createTunnel(GuacamoleConfiguration configuration) {
        try {
            InetGuacamoleSocket guacamoleSocket = new InetGuacamoleSocket("localhost", 4822);
            ConfiguredGuacamoleSocket socket = new ConfiguredGuacamoleSocket(guacamoleSocket, configuration);
            return new SimpleGuacamoleTunnel(socket);
        } catch (GuacamoleException e) {
            log.error("Error while creating tunnel: {}", e.getMessage());
            throw new IllegalArgumentException("Error creating tunnel");
        }
    }

    public void runDaemonThread(String deviceKey, String hostname, String port, String password) {

        if (connections.containsKey(deviceKey))
            return;
        GuacamoleConfiguration configuration = new GuacamoleConfiguration();
        configuration.setProtocol("vnc");
        configuration.setParameter("hostname", hostname);
        configuration.setParameter("port", port);
        configuration.setParameter("password", password);
        configuration.setParameter("color-depth", "16");
        configuration.setParameter("read-only", "true");
        configuration.setParameter("encodings", "Hextile");
        // Set recording path
        configuration.setParameter("recording-path", "/path/to/recordings");
        configuration.setParameter("create-recording-path", "true");

        GuacamoleTunnel tunnel = createTunnel(configuration);

        connections.put(deviceKey, ((ConfiguredGuacamoleSocket)tunnel.getSocket()).getConnectionID());

        StringBuilder buffer = new StringBuilder(BUFFER_SIZE);
        GuacamoleReader reader = tunnel.acquireReader();
        GuacamoleWriter writer = tunnel.acquireWriter();
        char[] readMessage;

        try {
            while ((readMessage = reader.read()) != null) {
                buffer.append(readMessage);
                if (!reader.available() || buffer.length() >= BUFFER_SIZE) {
                    String message = buffer.toString();
                    if (message.startsWith("4.sync"))
                        writer.write(message.toCharArray());
                    else
                        writer.write("3.ack,1.1,2.OK,1.0;".toCharArray());
                    buffer.setLength(0);
                }
            }
        } catch (GuacamoleConnectionClosedException ignored) {
            log.info("Connection closed for device {}", deviceKey);
        } catch (Exception e) {
            log.error("Daemon thread failed for device {}({}): {}", deviceKey, connections.get(deviceKey));
        } finally {
            connections.remove(deviceKey);
        }
    }

}