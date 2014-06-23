package maedn_server.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.client.ClientMessages;
import maedn_server.messages.client.Join;
import maedn_server.messages.client.Create;

public class Foyer implements IState {

    private class Connected {
        private boolean connected = false;
    }

    private final HashMap<Client, Connected> clients = new HashMap<>();
    private final Gson gson = new Gson();

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
        System.out.println("client get matches");
    }

    private void handleJoin(Client client, Action<Join> join) {
        System.out.println("client joins game: " + join.payload.matchId + " " + join.payload.nickname);
    }

    private void handleCreate(Client client, Action<Create> create) {
        System.out.println("client create game: " + create.payload.nickname);
    }

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

    private boolean isAction(String json) {
        return isMsgType(json, "action");
    }

    private boolean isResponse(String json) {
        return isMsgType(json, "response");
    }

    private boolean isMsgType(String json, String msgType) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(json, type);
        return (map.get(msgType) != null);
    }
}
