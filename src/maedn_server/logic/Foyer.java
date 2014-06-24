package maedn_server.logic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.client.ClientMessages;
import maedn_server.messages.client.Create;
import maedn_server.messages.client.Join;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.ServerMessages;

public class Foyer extends WebsocketReceiver {

    private class Connected {
        private boolean connected = false;
    }

    public Foyer() {
        Room r = new Room(cnt, this);
        rooms.put(cnt, r);
        r.addPlayer(new Client(r, null), "hans");
        r.addPlayer(new Client(r, null), "heiko");
    }

    private final HashMap<Client, Connected> clients = new HashMap<>();
    private final TreeMap<Integer, Room> rooms = new TreeMap<>();
    private int cnt = 0;

    public void registerClient(Client client) {
        clients.put(client, new Connected());
    }

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            switch (action.action) {
                case "connect":
                    handleConnect(client);
                    break;
                case "getMatches":
                    handleGetMatches(client);
                    break;
                case "join":
                    handleJoin(client, gson.fromJson(json, ClientMessages.getJoinType()));
                    break;
                case "create":
                    handleCreate(client, gson.fromJson(json, ClientMessages.getCreateType()));
                    break;
                default:
            }
        } else {
            if (isResponse(json)) {

            }
        }
    }

    private void handleConnect(Client client) {
        Connected con = clients.get(client);
        if (!con.connected) {
            System.out.println("not connected"); // TODO: remove, only for debugging
            con.connected = true;
            client.sendData(gson.toJson(CommonMessages.newSimpleResponse("connected")));
        } else {
            System.out.println("already connected"); // TODO: remove, only for debugging
        }
    }

    private void handleGetMatches(Client client) {
        List<MatchNode> matches = new LinkedList<>();
        for (Map.Entry<Integer, Room> r : rooms.entrySet()) {
            matches.add(r.getValue().getMatchNode());
        }
        client.sendData(gson.toJson(ServerMessages.newMatchesResponse(matches)));
    }

    private void handleJoin(Client client, Action<Join> join) {
        Room r = rooms.get(join.payload.matchId);
        if (r != null) {
            if (!r.isFull()) {
                if (r.isNicknameOk(join.payload.nickname)) {
                    addClientToRoom(client, r, join.payload.nickname);
                } else {
                    // nickname in use
                    client.sendData(gson.toJson(
                            CommonMessages.newMsgResponse("error",
                                    "Nickname already in use!")));
                }
            } else {
                // game is full
                client.sendData(gson.toJson(
                        CommonMessages.newMsgResponse("error",
                                "Match is full!")));
            }
        } else {
            // game noch available
            client.sendData(gson.toJson(
                    CommonMessages.newMsgResponse("error",
                            "Match does not exist!")));
        }
    }

    private void handleCreate(Client client, Action<Create> create) {
        Room r = new Room(cnt, this);
        addClientToRoom(client, r, create.payload.nickname);
    }

    private void addClientToRoom(Client client, Room r, String nickname) {
        rooms.put(cnt++, r);
        r.addPlayer(client, nickname);
        client.setLogic(r);
        r.notifyPlayer(client);
        r.notifyAllPlayer(client);
    }

}
