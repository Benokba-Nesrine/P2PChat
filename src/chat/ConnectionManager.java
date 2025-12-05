package chat;

import java.io.IOException;           
import java.net.ServerSocket;        
import java.net.Socket;            
import java.util.ArrayList;
import java.util.Collections;       
import java.util.List;
import P2P.chat.MessageReceiver;
public class ConnectionManager {

   
    private ServerSocket serverSocket;
    private Thread acceptThread;
    private boolean running = false;

    private final List<Peer> peers = Collections.synchronizedList(new ArrayList<>());
    private final PeerListener listener;
    public ConnectionManager(PeerListener listener) {
        this.listener = listener;   
    }

    public void startServer(int port) throws IOException {

        if (running) return;

        serverSocket = new ServerSocket(port);

        running = true;

        acceptThread = new Thread(() -> {

            System.out.println("[Server] Listening on port " + port);

            while (running) {
                try {

                    Socket socket = serverSocket.accept();
                    sendHandshake(socket);
                    startMessageReceiver(socket);
                    Peer peer = new Peer(socket);
                    peers.add(peer);

                    System.out.println("[Server] Accepted: " + peer);
                    if (listener != null) {
                        listener.onPeerConnected(peer); 
                    }

                } catch (IOException e) {
                    if (running)
                        e.printStackTrace();  
                }
            }
        });

        acceptThread.start();
    }

    public Peer connectToPeer(String host, int port) throws IOException {

        Socket socket = new Socket(host, port);
        sendHandshake(socket);
        startMessageReceiver(socket);
        Peer peer = new Peer(socket);
        peers.add(peer);

        System.out.println("[Client] Connected to: " + peer);

        if (listener != null) {
            listener.onPeerConnected(peer);
        }

        return peer;  
    }

    public List<Peer> getPeers() {
        return Collections.unmodifiableList(peers);
    }

    public void shutdown() {

        running = false;

        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}

        synchronized (peers) {
            for (Peer p : peers) {
                p.close(); 
            }
            peers.clear(); 
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
