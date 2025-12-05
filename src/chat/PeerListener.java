package chat; 
// This file is inside the "chat" package (same folder as your other classes).

// An interface is like a "contract" that says:
// "Any class that implements this must provide the onPeerConnected method."
public interface PeerListener {

    // This method will be called whenever a new peer connects.
    // ConnectionManager will run this method automatically.
    //
    // 'peer' is the new Peer object representing the connected user.
    void onPeerConnected(Peer peer);
}
