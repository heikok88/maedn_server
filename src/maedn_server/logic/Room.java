package maedn_server.logic;

import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.server.MatchNode;

public class Room implements IState {

    private final int id;
    private final Stack<Client> player;
    private final Foyer foyer;

    public Room(int id, Foyer foyer) {
        this.id = id;
        this.foyer = foyer;
        this.player = new Stack<>();
    }

    @Override
    public void reveiceData(Client client, String json) {

    }

    public boolean addPlayer(Client client) {
        boolean ret = false;
        if (player.size() < 4) {
            player.push(client);
            client.setLogic(this);
        }
        return ret;
    }

    public MatchNode getMatchNode() {
        return new MatchNode(id, player.size());
    }

}
