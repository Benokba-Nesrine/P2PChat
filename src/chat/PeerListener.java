package chat;

import P2P.chat.Peer;

public interface PeerListener {
    void onPeerConnected(Peer peer);
}
