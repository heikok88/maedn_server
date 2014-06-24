package maedn_server.logic;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.Response;
import maedn_server.messages.server.GameParticipants;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.Player;
import maedn_server.messages.server.ServerMessages;

public class Room extends WebsocketReceiver {

    private static String[] colors = {"red", "blue", "green", "yellow"};
    private final int id;
    private final Stack<Client> clients;
    private final Stack<Player> player;

    public Room(int id) {
        this.id = id;
        this.clients = new Stack<>();
        this.player = new Stack<>();
    }

    public boolean isFull() {
        return (clients.size() >= 4);
    }

    public boolean isNicknameOk(String nickname) {
        Enumeration<Player> p = player.elements();
        while (p.hasMoreElements()) {
            Player pl = p.nextElement();
            if (pl.nickname.equals(nickname)) {
                return false;
            }
        }
        return true;
    }

    public boolean addPlayer(Client client, String nickname) {
        boolean ret = false;
        if (clients.size() < 4) {
            ret = true;
            clients.push(client);
            client.setLogic(this);
            player.push(new Player(nickname, colors[player.size()]));
        }
        return ret;
    }

    public void notifyPlayer(Client client) {
        Response<GameParticipants> rs = ServerMessages.newJoinedResponse(
                id, getPlayersList());
        client.sendData(gson.toJson(rs));
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

    private List<Player> getPlayersList() {
        List<Player> l = new LinkedList<>();
        Enumeration<Player> p = player.elements();
        while (p.hasMoreElements()) {
            l.add(p.nextElement());
        }
        return l;
    }

    public MatchNode getMatchNode() {
        return new MatchNode(id, clients.size());
    }

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            switch (action.action) {
                case "leave":
                    handleLeave(client);
                    break;
                case "ready":
                    handleReady(client);
                    break;
                default:
                    // TODO: handle forbidden json object
            }
        } else if (isResponse(json)) {
            // TODO
            Response response = gson.fromJson(json, Response.class);
            switch (response.response) {
                default:
                    // TODO: handle forbidden json object
            }
        }
    }

    private void handleLeave(Client client) {
        // TODO
    }

    private void handleReady(Client client) {
        // TODO
    }
}

