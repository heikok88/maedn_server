package maedn_server.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import maedn_server.Client;
import maedn_server.logic.luts.Board;
import maedn_server.logic.luts.Fields;
import maedn_server.logic.luts.Goal;
import maedn_server.logic.luts.Start;
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
    private boolean newFigure = false;

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

        sendToAllPlayer(ServerMessages.newMatchUpdateResponse(activePlayerNickname(), getAllFigures()));
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
        lastEyes = 0;
        playerID = (playerID + 1) % player.size();
    }

    private String activePlayerNickname() {
        return player.get(playerID).nickname;
    }

    public void removeClient(Client client) {
        int index = clients.indexOf(client);
        if (index != -1) {
            clients.remove(index);
            client.sendData(gson.toJson(CommonMessages.newSimpleResponse("left")));
            Foyer.getFoyerInstance().registerClient(client, true);
            if (clients.size() > 0) { // TODO
                start.remove(index);
                goal.remove(index);
                board.removePlayerFigures(player.get(index).nickname);
                player.remove(index);
                if (playerID == index) {
                    nextPlayer();
                }
                sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                        activePlayerNickname(), getAllFigures()));
            }
        }
    }

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            switch (action.action) {
                case "leave":
                    handleLeave(client);
                    break;
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

    private void handleLeave(Client client) {
        removeClient(client);
    }

    private void handleRollDice(Client client) {
        lastEyes = (lastEyes == 0) ? (int) (Math.random() * 6 + 1) : lastEyes;
        client.sendData(gson.toJson(ServerMessages.newRollDiceResponse(
                activePlayerNickname(), lastEyes)));
        sendToAllPlayer(ServerMessages.newRollDiceAction(
                activePlayerNickname(), lastEyes), client);
        if (!moveAbleFigure()) {
            if (lastEyes == 6) {
                cnt = 0;
                newFigure = setFigureOnStartPosition();
            } else {
                cnt++;
            }
            lastEyes = 0;
            if (cnt == 3) {
                cnt = 0;
                nextPlayer();
                sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                        activePlayerNickname(), getAllFigures()));
            }
        } else {
            if (lastEyes == 6) {
                if (!newFigure) {
                    newFigure = setFigureOnStartPosition();
                }
            }
        }
    }

    private void handleMove(Client client, Action<Move> move) {
        int id = clients.indexOf(client);
        if (id == playerID && lastEyes != 0) {
            if (moveFigure(move.payload)) {
                if (lastEyes != 6) {
                    nextPlayer();
                }
                List<Figure> l = getAllFigures();
                client.sendData(gson.toJson(ServerMessages.
                        newMatchUpdateResponse(activePlayerNickname(), l)));
                sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                        activePlayerNickname(), l), client);
                return;
            }
        }
        client.sendData(gson.toJson(CommonMessages.newMsgResponse(
                "error", "Illegal move!")));
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

    private int startPos() {
        return playerID * 10;
    }

    private int startPosX() {
        return board.getXY(startPos()).get(0);
    }

    private int startPosY() {
        return board.getXY(startPos()).get(1);
    }

    private int endPos() {
        return (startPos() + 40 - 1) % 40;
    }

    private boolean setFigureOnStartPosition() {
        boolean bool = false;
        Figure f = start.get(playerID).getFigure();
        if (f != null) {
            RetClass ret = getFigure(startPosX(), startPosY());
            if (!ret.own) {
                if (ret.figure != null) {
                    setFigureToStart(ret.figure);
                }
                board.setFigure(startPos(), f);
                sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                        activePlayerNickname(), getAllFigures()));
                bool = true;
            } else {
                start.get(playerID).setFigure(f);
            }
        }
        return bool;
    }

    private void setFigureToStart(Figure f) {
        String nick = f.nickname;
        for (int i = 0; i < start.size(); i++) {
            if (start.get(i).getNickname().equals(nick)) {
                start.get(i).setFigure(f);
                break;
            }
        }
    }

    private boolean moveFigure(Move move) {
        return moveFigure(move.fromX, move.fromY, move.toX, move.toY);
    }

    private boolean moveFigure(int fromX, int fromY, int toX, int toY) {
        RetClass rt1 = getFigure(fromX, fromY);
        RetClass rt2 = getFigure(toX, toY);

        Figure f1 = rt1.figure, f2 = rt2.figure;
        Fields fi1 = rt1.field, fi2 = rt2.field;

        List<Integer> xy = board.getXY(startPos() + lastEyes);
        RetClass r3 = getFigure(xy.get(0), xy.get(1));

        if (rt1.own) {
            if (!rt2.own) {
                int i1 = fi1.getIndex(fromX, fromY);
                boolean c1 = !newFigure; // normal move
                boolean c2 = (newFigure && i1 == startPos()); // zugzwang
                boolean c3 = (newFigure && r3.own); // cancels zugzwang
                if (c1 || c2 || c3) {
                    newFigure = false;
                    int i2 = fi2.getIndex(toX, toY);
                    if (fi1 == fi2) { // board to board or goal to goal
                        if (((i1 + lastEyes) % 40) == i2 && ((i1 < endPos()) ? i2 <= endPos() : true)) {
                            fi2.setFigure(i2, f1);
                            if (f2 != null) {
                                setFigureToStart(f2);
                            }
                            return true;
                        }
                    } else { // board to goal
                        if (fi2 != null) { // own goal
                            if ((i1 + lastEyes == i2 + endPos() + 1) && (f2 == null)) {
                                fi2.setFigure(i2, f1);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private class RetClass {

        public final Figure figure;
        public final Fields field;
        public final boolean own;

        public RetClass(Figure figure, Fields field, boolean own) {
            this.figure = figure;
            this.field = field;
            this.own = own;
        }
    }

    private RetClass getFigure(int x, int y) {
        Figure figure = null;
        Fields field = null;

        int index = board.getIndex(x, y);
        if (index != -1) {
            field = board;
        } else {
            Goal g = goal.get(playerID);
            index = g.getIndex(x, y);
            if (index != -1) {
                field = g;
            }
        }
        if (field != null) {
            figure = field.getFigure(index);
            if (figure != null && figure.nickname.equals(activePlayerNickname())) {
                return new RetClass(figure, field, true);
            }
        }
        return new RetClass(figure, field, false);
    }

}
