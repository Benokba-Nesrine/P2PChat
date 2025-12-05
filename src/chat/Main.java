package chat;
import P2P.chat.MessageReceiver;
public class Main implements PeerListener { 
    public static void main(String[] args) throws Exception {

       Main mainInstance = new Main();
        ConnectionManager cm = new ConnectionManager(mainInstance);
   

        // Start server on port 5000
        cm.startServer(2005);

    }
        @Override
    public void onPeerConnected(Peer peer) {
        System.out.println("New peer connected: " + peer.getIp() + ":" + peer.getPort());

        new Thread(new MessageReceiver(peer.getSocket(), new MessageReceiver.Callback() {
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
