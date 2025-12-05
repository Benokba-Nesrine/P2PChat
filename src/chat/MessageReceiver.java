package chat;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MessageReceiver implements Runnable {

    private final Socket socket;
    private String peerName = "stranger";
    private final Callback callback;

    public interface Callback {
        void onMessage(String name, String text, String time);
        void onDisconnect(String ip, int port);
    }

    public MessageReceiver(Socket socket, Callback callback) {
        this.socket = socket;
        this.callback = callback;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            while (!socket.isClosed()) {
                int len = in.readInt();
                if (len <= 0 || len > 1_000_000) break;

                byte[] buf = new byte[len];
                in.readFully(buf);

                String json = new String(buf, StandardCharsets.UTF_8);
                Map<String, String> msg = parse(json);

                if ("hello".equals(msg.get("type"))) {
                    peerName = msg.get("name");
                    System.out.println("[i] " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + " → " + peerName);
                    continue;
                }

                String name = msg.getOrDefault("name", peerName);
                String text = msg.get("text");
                String time = msg.getOrDefault("time", new Date().toString().substring(11, 19));

          
                System.out.println("[" + time + "] " + name + ": " + text);

           
                ChatHistory.addMessage(name, text);

                if (callback != null) {
                    callback.onMessage(name, text, time);
                }
            }
        } catch (Exception ignored) {
       
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
            if (callback != null) {
                callback.onDisconnect(socket.getInetAddress().getHostAddress(), socket.getPort());
            }
        }
    }

    private Map<String, String> parse(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        for (String part : json.split(",")) {
            String[] kv = part.split(":", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }
}
