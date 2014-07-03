package maedn_server.logic;

import maedn_server.Client;

public class PlayerTimeOut extends java.util.TimerTask {

    private final Client client;

    public PlayerTimeOut(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Player Timeout: Client (" + client.me + ") Receiver: " + client.getReceiver() + " Timer: " + client.getTimer().toString());
        this.client.getReceiver().removeClient(client);
    }
}
