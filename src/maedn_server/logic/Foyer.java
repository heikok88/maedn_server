package maedn_server.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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

public class Foyer implements IState {

    private class Connected {
        private boolean connected = false;
    }
    
    public Foyer() {
        Room r = new Room(cnt, this);
        rooms.put(cnt++, r);
        r.addPlayer(new Client(this, null), new Action<Create>("create", new Create("peter")));
        r.addPlayer(new Client(this, null), new Action<Create>("create", new Create("hans")));
        r.addPlayer(new Client(this, null), new Action<Create>("create", new Create("paul")));
    }
    
    private final Gson gson = new Gson();
    private final HashMap<Client, Connected> clients = new HashMap<>();
    private final TreeMap<Integer, Room> rooms = new TreeMap<>();
    private int cnt = 0;

    private void handleConnect(Client client) {
        Connected con = clients.get(client);
        if (!con.connected) {
            System.out.println("not connected");
            con.connected = true;
            client.sendData(gson.toJson(CommonMessages.newSimpleResponse("connected")));
        } else {
            System.out.println("already connected");
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
        System.out.println("client joins game: " + join.payload.matchId + " " + join.payload.nickname);
    }

    private void handleCreate(Client client, Action<Create> create) {
        Room r = new Room(cnt, this);
        rooms.put(cnt++, r);
        r.addPlayer(client, create);
        client.setLogic(r);
        r.notifyPlayer();
    }

    public void registerClient(Client client) {
        clients.put(client, new Connected());
    }

    @Override
    public void reveiceData(Client client, String json) {
        System.out.println("Blaaaaaaaaaaaaaaaaa: " + json);
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

    private boolean isAction(String json) {
        return isMsgType(json, "action");
    }

    private boolean isResponse(String json) {
        return isMsgType(json, "response");
    }

    private boolean isMsgType(String json, String msgType) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(json, type);
        return (map.get(msgType) != null);
    }
}
