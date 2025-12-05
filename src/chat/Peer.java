package chat;

import java.io.*;
import java.net.Socket;

public class Peer {
    private final Socket socket;

    public Peer(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public InputStream in() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream out() throws IOException {
        return socket.getOutputStream();
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }

    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    @Override
    public String toString() {
        return socket.getRemoteSocketAddress().toString();
    }
}
