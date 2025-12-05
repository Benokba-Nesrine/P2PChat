package chat;

public class Main {
    public static void main(String[] args) throws Exception {

        ConnectionManager cm = new ConnectionManager(peer -> {
            System.out.println("New peer connected: " + peer);
        });

        // Start server on port 5000
        cm.startServer(2005);

    }
}
