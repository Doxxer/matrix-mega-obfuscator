package ru.spbau.mit.networks.client;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ConnectToServer extends ServerAction {

    public ConnectToServer(String actionName) {
        super(actionName);
    }

    @Override
    protected void makeSocketAction(SocketChannel channel) throws IOException {
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
    }
}
