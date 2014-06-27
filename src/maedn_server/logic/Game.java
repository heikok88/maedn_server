package maedn_server.logic;

import maedn_server.logic.luts.Goal;
import maedn_server.logic.luts.Start;
import maedn_server.logic.luts.Board;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.Response;
import maedn_server.messages.client.ClientMessages;
import maedn_server.messages.client.Move;
import maedn_server.messages.server.Figure;
import maedn_server.messages.server.Player;
import maedn_server.messages.server.ServerMessages;

public class Game extends WebsocketReceiver {

    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final Stack<Start> start;
    private final Stack<Goal> goal;
    private final Board board;

    private int playerID = 0;
    private int lastEyes = 0;
    private int cnt = 0;

    public Game(Stack<Client> clients, Stack<Player> player) {
        this.clients = clients;
        this.player = player;

        Action ac = CommonMessages.newSimpleAction("matchStart");

        goal = new Stack<>();
        start = new Stack<>();
        for (int i = 0; i < player.size(); i++) {
            clients.get(i).setReceiver(this);
            clients.get(i).sendData(gson.toJson(ac));
            goal.push(new Goal(i));
            start.push(new Start(i, player.get(i).nickname));
        }

        board = new Board();

        sendToAllPlayer(ServerMessages.newMatchUpdateResponse(activePlayer(), getAllFigures()));
    }

    private void sendToAllPlayer(Object o) {
        sendToAllPlayer(o, null);
    }

    private void sendToAllPlayer(Object o, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            Client cl = clients.get(i);
            if (client != cl) {
                cl.sendData(gson.toJson(o));
            }
        }
    }

    public List<Figure> getAllFigures() {
        List<Figure> figures = new ArrayList<>();
        figures.addAll(board.getAllFigures());

        for (int i = 0; i < clients.size(); i++) {
            figures.addAll(start.get(i).getAllFigures());
            figures.addAll(goal.get(i).getAllFigures());
        }

        return figures;
    }

    private void nextPlayer() {
        playerID = (playerID + 1) % player.size();
    }

    private String activePlayer() {
        return player.get(playerID).nickname;
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
        int index = clients.indexOf(client);
        String nickname = (index != -1) ? player.get(index).nickname : null;
        if (nickname != null) {
            lastEyes = (lastEyes == 0) ? (int) (Math.random() * 6 + 1) : lastEyes;
            client.sendData(gson.toJson(ServerMessages.newRollDiceResponse(
                    nickname, lastEyes)));
            sendToAllPlayer(ServerMessages.newRollDiceAction(
                    nickname, lastEyes), client);
            if (!moveAbleFigure()) {
                if (lastEyes == 6) {
                    cnt = 0;

                } else {
                    cnt++;
                }
                lastEyes = 0;
            }
        }
    }

    private void handleMove(Client client, Action<Move> move) {
        int player = clients.indexOf(client);
        if (player == playerID && lastEyes != 0) {

        } else {
            client.sendData(gson.toJson(CommonMessages.newMsgResponse(
                    "error", "Illegal move!")));
        }
    }

    private boolean moveAbleFigure() {
        int free = 4 - start.get(playerID).size();
        if (free == 0) {
            return false;
        }
        if (goal.get(playerID).size() == free && !goal.get(playerID).canMove()) {
            return false;
        }
        return true;
    }

}
