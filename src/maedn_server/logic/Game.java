package maedn_server.logic;

import java.util.Enumeration;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.Response;
import maedn_server.messages.client.ClientMessages;
import maedn_server.messages.client.Move;
import maedn_server.messages.server.Player;

public class Game extends WebsocketReceiver {

    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final int id;

    public Game(int id, Stack<Client> clients, Stack<Player> player) {
        this.id = id;
        this.clients = clients;
        this.player = player;
        
        Action ac = CommonMessages.newSimpleAction("matchStart");
        
        Enumeration<Client> c = clients.elements();
        Client client;
        while (c.hasMoreElements()) {
            client = c.nextElement();
            client.setReceiver(this);
            client.sendData(gson.toJson(ac));
        }
    }

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            switch (action.action) {
                case "rollDice":
                    handleRollDice(client);
                    break;
                case "move":
                    handleMove(client, gson.fromJson(json, ClientMessages.getMoveType()));
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

    private void handleRollDice(Client client) {
        
    }

    private void handleMove(Client client, Action<Move> move) {
        
    }

}
