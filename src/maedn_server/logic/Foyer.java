package maedn_server.logic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.Response;
import maedn_server.messages.client.ClientMessages;
import maedn_server.messages.client.Create;
import maedn_server.messages.client.Join;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.ServerMessages;

public class Foyer extends WebsocketReceiver {

    private static Foyer singleton;

    public static Foyer getFoyerInstance() {
        if (singleton == null) {
            singleton = new Foyer();
        }
        return singleton;
    }
    
    private class Connected {
        private Connected(boolean con) {
            this.connected = con;
        }
        private boolean connected;
    }

    private final HashMap<Client, Connected> clients = new HashMap<>();
    private final TreeMap<Integer, Room> rooms = new TreeMap<>();
    private int cnt = 0;

    private Foyer() {
//        Room r = new Room(cnt);
//        rooms.put(cnt, r);
//        r.addPlayer(new Client(null), "hans");
//        r.addPlayer(new Client(null), "heiko");
    }

    public void registerClient(Client client) {
        registerClient(client, false);
    }
    
    public void registerClient(Client client, boolean connected) {
        clients.put(client, new Connected(connected));
        client.setReceiver(this);
    }
    
    public void deregisterClient(Client client) {
        clients.remove(client);
    }
    
    public void addRoom(Room r) {
        rooms.put(cnt++, r);
    }
    
    public void removeRoom(Room r) {
        for (Map.Entry<Integer, Room> entry : rooms.entrySet()) {
            if (entry.getValue() == r) {
                rooms.remove(entry.getKey());
                r = null;
                break;
            }
        }
        
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

    private void handleConnect(Client client) {
        Connected con = clients.get(client);
        if (!con.connected) {
            con.connected = true;
            client.sendData(gson.toJson(CommonMessages.newSimpleResponse("connected")));
        } else {
            // TODO
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
                    deregisterClient(client);
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
        Room r = new Room(cnt);
        addRoom(r);
        addClientToRoom(client, r, create.payload.nickname);
        deregisterClient(client);
    }

    private void addClientToRoom(Client client, Room r, String nickname) {
        r.addPlayer(client, nickname);
        r.notifyJoinedPlayer(client);
        r.notifyAllPlayer(client);
    }

}
