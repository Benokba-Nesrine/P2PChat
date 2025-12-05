package P2P;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Node {

    private String username;
    private int listeningPort;
    private ServerSocket server;
    private final Map<String, Peer> peers = new ConcurrentHashMap<>();

    public Node(String username, int listeningPort) {
        this.username = username;
        this.listeningPort = listeningPort;
    }

    
    public void startServer() {
        new Thread(() -> {
            try {
                server = new ServerSocket(listeningPort);
                System.out.println("Node listening on port " + listeningPort);

                while (true) {
                    Socket socket = server.accept();

                    
                    Peer peer = new Peer(socket);

                    
                    addPeer(peer);

            
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

      
    public void addPeer(Peer p) {
        peers.put(p.getPeerId(), p);
        System.out.println("Peer added: " + p.getPeerId());
    }

    public void removePeer(String id) {
        Peer p = peers.remove(id);
        if (p != null) {
            p.close();
            System.out.println("Peer removed: " + id);
        }
    }

    
    public void listPeers() {
        if (peers.isEmpty()) {
            System.out.println("No connected peers.");
            return;
        }

        System.out.println("Connected peers:");
        peers.forEach((id, peer) -> {
            System.out.println(id + " => " + peer.getSocket().getRemoteSocketAddress());
        });
    }

    public Map<String, Peer> getPeers() {
        return peers;
    }

    public String getUsername() {
        return username;
    }
}
