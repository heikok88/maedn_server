package maedn_server.logic;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.client.Create;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.Player;

public class Room implements IState {

    private static String[] colors = {"red" , "blue", "green", "yellow"};
    private final int id;
    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final Foyer foyer;

    public Room(int id, Foyer foyer) {
        this.id = id;
        this.foyer = foyer;
        this.clients = new Stack<>();
        this.player = new Stack<>();
    }

    @Override
    public void reveiceData(Client client, String json) {

    }

    public boolean addPlayer(Client client, Action<Create> create) {
        boolean ret = false;
        if (clients.size() < 4) {
            clients.push(client);
            client.setLogic(this);
            player.push(new Player(create.payload.nickname, colors[player.size()]));
        }
        return ret;
    }

    public void notifyPlayer() {
        List<Client> l = new LinkedList<>();
        Enumeration<Client> c = clients.elements();
        while (c.hasMoreElements()) {
            // TODO
        }
    }

    public MatchNode getMatchNode() {
        return new MatchNode(id, clients.size());
    }

}
