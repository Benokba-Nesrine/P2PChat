package chat;

import chat.Peer;

public interface PeerListener {
    void onPeerConnected(Peer peer);
}
