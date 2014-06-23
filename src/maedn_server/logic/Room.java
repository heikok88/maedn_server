package maedn_server.logic;

import com.google.gson.Gson;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.Response;
import maedn_server.messages.client.Create;
import maedn_server.messages.server.GameParticipants;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.Player;
import maedn_server.messages.server.ServerMessages;

public class Room implements IState {

    private static String[] colors = {"red", "blue", "green", "yellow"};
    private final int id;
    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final Foyer foyer;
    private final Gson gson;

    public Room(int id, Foyer foyer, Gson gson) {
        this.id = id;
        this.foyer = foyer;
        this.clients = new Stack<>();
        this.player = new Stack<>();
        this.gson = gson;
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

    private List<Player> getPlayersList() {
        List<Player> l = new LinkedList<>();
        Enumeration<Player> p = player.elements();
        while (p.hasMoreElements()) {
            l.add(p.nextElement());
        }
        return l;
    }

    public void notifyAllPlayer(Client client) {
        Action<GameParticipants> ac = ServerMessages.newUpdatePlayersAction(
                id, getPlayersList());
        Enumeration<Client> c = clients.elements();
        while (c.hasMoreElements()) {
            Client cl = c.nextElement();
            if (cl != client) {
                cl.sendData(gson.toJson(ac));
            }
        }
    }

    public void notifyPlayer(Client client) {
        Response<GameParticipants> rs = ServerMessages.newUpdatePlayersResponse(
                id, getPlayersList());
        client.sendData(gson.toJson(rs));
    }

    public MatchNode getMatchNode() {
        return new MatchNode(id, clients.size());
    }

}
