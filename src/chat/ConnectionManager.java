package chat;

// Import needed Java classes
import java.io.IOException;            // For input/output errors
import java.net.ServerSocket;         // For the server that listens for connections
import java.net.Socket;               // For each connection to another peer
import java.util.ArrayList;
import java.util.Collections;         // To make list thread-safe 
import java.util.List;
import P2P.chat.MessageReceiver;
public class ConnectionManager {

    // The server socket that listens for incoming connections
    private ServerSocket serverSocket;

    // A separate thread that will run the "accept loop"
    private Thread acceptThread;

    // A boolean flag that says if the server is running
    private boolean running = false;

    // A list of all connected peers, made thread-safe
    // final = you cannot change the list reference
    private final List<Peer> peers = Collections.synchronizedList(new ArrayList<>());

    // A listener (callback) that will be called when a new peer connects
    private final PeerListener listener;

    // Constructor
    // You pass a listener, and we store it for later
    public ConnectionManager(PeerListener listener) {
        this.listener = listener;   // Save the callback
    }

    // Start the server on the given port
    public void startServer(int port) throws IOException {

        // If already running, do nothing
        if (running) return;

        // Create the server socket
        serverSocket = new ServerSocket(port);

        // Mark the server as running
        running = true;

        // Create the accept thread
        acceptThread = new Thread(() -> {

            System.out.println("[Server] Listening on port " + port);

            // The accept loop: runs as long as the server is running
            while (running) {
                try {

                    // Accept a new connection -> returns a Socket
                    Socket socket = serverSocket.accept();
                    sendHandshake(socket);
                    startMessageReceiver(socket);
                    // Wrap it in a Peer object
                    Peer peer = new Peer(socket);

                    // Add to the list of peers
                    peers.add(peer);

                    System.out.println("[Server] Accepted: " + peer);

                    // If we have a listener (callback), notify it
                    if (listener != null) {
                        listener.onPeerConnected(peer); 
                    }

                } catch (IOException e) {
                    // If we are still running and something breaks, print error
                    if (running)
                        e.printStackTrace();  // Print error details
                }
            }
        });

        // Start the accept thread
        acceptThread.start();
    }

    // Connect to another peer (client side)
    public Peer connectToPeer(String host, int port) throws IOException {

        // Open a socket to the host
        Socket socket = new Socket(host, port);
        sendHandshake(socket);
        startMessageReceiver(socket);
        // Wrap it in a Peer object
        Peer peer = new Peer(socket);

        // Add to list
        peers.add(peer);

        System.out.println("[Client] Connected to: " + peer);

        // Notify listener
        if (listener != null) {
            listener.onPeerConnected(peer);
        }

        return peer;  // Return this peer so others can talk to it
    }

    // Return the list of peers (read-only)
    public List<Peer> getPeers() {
        return Collections.unmodifiableList(peers);
    }

    // Stop everything and clean up
    public void shutdown() {

        // Stop the running flag
        running = false;

        // Close the server socket
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}

        // Close all peer sockets
        synchronized (peers) {
            for (Peer p : peers) {
                p.close();  // Close each peer socket
            }
            peers.clear(); // Clear the list
        }

        System.out.println("[ConnectionManager] Shutdown complete.");
    }
        private void sendHandshake(Socket socket) {
        try {
            String hello = "{\"type\":\"hello\",\"name\":\"" + Main.myName + "\"}";
            byte[] data = hello.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(data.length);
            out.write(data);
        } catch (Exception ignored) {}
    }

    private void startMessageReceiver(Socket socket) {
        new Thread(new MessageReceiver(socket, new MessageReceiver.Callback() {
            @Override
            public void onMessage(String name, String text, String time) {
                System.out.println("[" + time + "] " + name + ": " + text);
            }

            @Override
            public void onDisconnect(String ip, int port) {
                System.out.println("Disconnected: " + ip + ":" + port);
            }
        })).start();
    }
}
