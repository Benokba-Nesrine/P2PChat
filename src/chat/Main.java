package chat;

import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main implements PeerListener {

    public static String myName = "You";          
    private final ConnectionManager connectionManager;
    private final Scanner scanner = new Scanner(System.in);

    public Main() {
        this.connectionManager = new ConnectionManager(this);
    }

    public static void main(String[] args) throws Exception {
        System.out.print("Enter your name: ");
        myName = new Scanner(System.in).nextLine().trim();
        if (myName.isEmpty()) myName = "User";

        Main app = new Main();

        app.connectionManager.startServer(2000);

        System.out.println("\nP2P Chat started – you are: " + myName);
        ChatHistory.showHistory();
        System.out.println("Waiting for peers... (type 'connect <ip>' or 'exit')");

        while (true) {
            String input = app.scanner.nextLine().trim();

            if (input.startsWith("connect ")) {
                String ip = input.substring(8).trim();
                try {
                    app.connectionManager.connectToPeer(ip, 2000);
                    System.out.println("Connecting to " + ip + "...");
                } catch (Exception e) {
                    System.out.println("Connection failed");
                }
            }
            else if (input.equalsIgnoreCase("exit")) {
                app.connectionManager.shutdown();
                System.out.println("Goodbye!");
                break;
            }
            else if (!input.isEmpty()) {
                // SEND MESSAGE TO ALL CONNECTED PEERS
                String time = new SimpleDateFormat("HH:mm").format(new Date());
                String json = "{\"name\":\"" + myName + "\",\"text\":\"" + input + "\",\"time\":\"" + time + "\"}";
                byte[] data = json.getBytes(java.nio.charset.StandardCharsets.UTF_8);

                for (Peer p : app.connectionManager.getPeers()) {
                    try {
                        DataOutputStream out = new DataOutputStream(p.out());
                        out.writeInt(data.length);
                        out.write(data);
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    @Override
    public void onPeerConnected(Peer peer) {
        System.out.println("Peer connected: " + peer.getIp() + ":" + peer.getPort());

        new Thread(new MessageReceiver(peer.getSocket(), new MessageReceiver.Callback() {
            @Override
            public void onMessage(String name, String text, String time) {
                System.out.println("[" + time + "] " + name + ": " + text);
            }

            @Override
            public void onDisconnect(String ip, int port) {
                System.out.println("Peer disconnected: " + ip + ":" + port);
            }
        })).start();
    }
}
