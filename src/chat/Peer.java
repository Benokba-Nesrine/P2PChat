package chat;  
// This file belongs to the "chat" package (folder).  
// It allows ConnectionManager and Main to find this class.

import java.io.IOException;      // Needed for input/output errors
import java.io.InputStream;      // Needed to read data from the peer
import java.io.OutputStream;     // Needed to send data to the peer
import java.net.Socket;          // Represents a network connection to another machine
import P2P.chat.MessageReceiver;
public class Peer {
    // This is the socket connection to the other peer.
    // final = we cannot replace the socket with another one once it's created.
    private final Socket socket;

    // Constructor: when we create a Peer, we give it a socket.
    // This "wraps" the socket inside this Peer object.
    public Peer(Socket socket) {
        this.socket = socket;
    }
        public Socket getSocket() {
        return socket;       
    }
    public Socket getSocket getSocket() {
        return socket;
    }
    // Returns the InputStream of the socket.
    // This is used to READ data that the other peer sends to us.
    public InputStream in() throws IOException {
        return socket.getInputStream();
    }

    // Returns the OutputStream of the socket.
    // This is used to SEND data to the other peer.
    public OutputStream out() throws IOException {
        return socket.getOutputStream();
    }

    // Close the connection to this peer.
    // We use a try/catch because closing might throw an IOException.
    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
            // If closing fails, we ignore the error because there's nothing to fix here.
        }
    }

    // This method controls what gets printed when we print a Peer object.
    // Example: "/127.0.0.1:5000"
    @Override
    public String toString() {
        return socket.getRemoteSocketAddress().toString();
    }
}
