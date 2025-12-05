package P2P;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public class Peer {

    private final String peerId;
    private final Socket socket;

    public Peer(Socket socket) {
        this.peerId = UUID.randomUUID().toString();
        this.socket = socket;
    }

    public String getPeerId() {
        return peerId;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}
