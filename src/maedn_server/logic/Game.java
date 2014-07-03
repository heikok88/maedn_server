package maedn_server.logic;

import java.util.ArrayList;
import java.util.LinkedList;
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

    private final int GAME_ID;
    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final Stack<Start> start;
    private final Stack<Goal> goal;
    private final List<String> done;
    private final Board board;
    private final ResponseManager responseManager = new ResponseManager(this);

    private int playerID = 0;
    private int lastEyes = 0;
    private int cnt = 0;
    private boolean newFigure = false;
    private int playerDone = -1;
    private static final long timeout = (1000L * 60);

    public Game(int id, Stack<Client> clients, Stack<Player> player) {
        this.GAME_ID = id;
        this.clients = clients;
        this.player = player;

        Action ac = CommonMessages.newSimpleAction("matchStart");

        goal = new Stack<>();
        start = new Stack<>();
        done = new LinkedList<>();

        for (int i = 0; i < player.size(); i++) {
            clients.get(i).setReceiver(this);
            clients.get(i).sendData(gson.toJson(ac));
            responseManager.addTask(clients.get(i), ac);
            goal.push(new Goal(i));
            start.push(new Start(i, player.get(i).nickname));
        }
        board = new Board();

        sendToAllPlayer(ServerMessages.newMatchUpdateResponse(activePlayerNickname(), getAllFigures()));
        startGameTimer(clients.get(0));
    }

    private void sendToAllPlayer(Object o) {
        sendToAllPlayer(o, null);
    }

    private void sendToAllPlayer(Object o, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            Client cl = clients.get(i);
            if (client != cl) {
                if (o instanceof Action) {
                    responseManager.addTask(cl, (Action) o);
                } 

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

    private void nextPlayer(boolean notify) {
        nextPlayer(notify, null);
    }

    private void nextPlayer(boolean notify, Client client) {
        int count = 0;

        if (playerID < clients.size()) {
            Client c = clients.get(playerID);
            if (c != null) {
                c.stopTimer();
            }
        }

        do {
            playerID += 1;
            if (playerID >= player.size()) {
                playerID = 0;
            }
            count++;
        } while (done.contains(activePlayerNickname()) && count < player.size());

        lastEyes = 0;
        newFigure = false;

        clients.get(playerID).startTimer();

        if (notify) {
            sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                    activePlayerNickname(), getAllFigures()), client);
        }
    }

    private String activePlayerNickname() {
        return player.get(playerID).nickname;
    }

    @Override
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
                    nextPlayer(false);
                }
                sendToAllPlayer(ServerMessages.newUpdatePlayersAction(GAME_ID, player));
                sendToAllPlayer(ServerMessages.newMatchUpdateAction(
                        activePlayerNickname(), getAllFigures()));
                matchDone();
            }
        }
    }

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            //client.restartTimer();
            switch (action.action) {
                case "leave":
                    handleLeave(client);
                    break;
                case "rollDice":
                    startGameTimer(client);
                    handleRollDice(client);
                    break;
                case "move":
                    startGameTimer(client);
                    handleMove(client, gson.fromJson(json, ClientMessages.getMoveType()));
                    break;
                default:
                // TODO: handle forbidden json object
            }
        } else if (isResponse(json)) {
            // TODO
            Response response = gson.fromJson(json, Response.class);
            switch (response.response) {
                case "playersUpdated":
                    responseManager.attendResponse(client, "playersUpdated");
                case "matchStarted":
                    responseManager.attendResponse(client, "matchStarted");
                    break;
                case "matchUpdated":
                    responseManager.attendResponse(client, "matchUpdated");
                    break;
                case "diceRolled":
                    responseManager.attendResponse(client, "diceRolled");
                    break;
                case "playerDone":
                    responseManager.attendResponse(client, "playerDone");
                    break;
                case "matchDone":
                    responseManager.attendResponse(client, "matchDone");
                    break;
                default:
                // TODO: handle forbidden json object
            }
        }
    }

    private void handleLeave(Client client) {
        client.stopTimer();
        removeClient(client);
    }

    private void startGameTimer(Client client) {
        int index = clients.indexOf(client);
        if (index != 1 && index == playerID) {
            if (client.getTimer() == null) {
                client.startTimer();
            } else {
                client.restartTimer();
            }
        }
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
                if (lastEyes != 6) {
                    nextPlayer(true);
                }
            }
        } else {
            if (lastEyes == 6) {
                if (!newFigure) {
                    newFigure = setFigureOnStartPosition();
                    if (newFigure) {
                        lastEyes = 0;
                    }
                }
            }
            if (!newFigure && !canMove()) {
                nextPlayer(true);
            }
        }
    }

    private void handleMove(Client client, Action<Move> move) {
        int id = clients.indexOf(client);
        if (id == playerID && lastEyes != 0) {
            if (moveFigure(move.payload)) {
                if (lastEyes != 6) {
                    nextPlayer(true, client);
                }
                lastEyes = 0;

                client.sendData(gson.toJson(ServerMessages.
                        newMatchUpdateResponse(activePlayerNickname(),
                                getAllFigures())));
                matchDone();
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
        if (goal.get(playerID).size() == free
                && !goal.get(playerID).moveAbleFigures()) {
            return false;
        }
        return true;
    }

    private boolean canMove() {
        int free = 4 - start.get(playerID).size();
        if (free == 0) {
            return false;
        }
        if (goal.get(playerID).size() == free
                && !goal.get(playerID).canMove(lastEyes)) {
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
                        if (((i1 + lastEyes) % 40) == i2 && ((i1 <= endPos()) ? i2 <= endPos() && i1 < i2 : true)) {
                            fi2.setFigure(i2, f1);
                            if (f2 != null) {
                                setFigureToStart(f2);
                            }
                            return true;
                        }
                    } else if (fi1 instanceof Board && fi2 instanceof Goal) { // board to goal
                        if ((i1 + lastEyes == i2 + endPos() + 1) && (f2 == null)) {
                            fi1.delFigure(i1);
                            fi2.setFigure(i2, f1);
                            if (fi2.size() == 4) {
                                playerDone = playerID;
                            }
                            return true;
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

    public void playerDone() {
        if (playerDone != -1) {
            done.add(player.get(playerDone).nickname);
            sendToAllPlayer(ServerMessages.newPlayerDoneAction(
                    player.get(playerDone).nickname));
            if (playerDone == playerID) {
                nextPlayer(true);
            }
            playerDone = -1;
        }
    }

    public void matchDone() {
        int remainingPlayers = 0;
        String tmp = "";

        playerDone();
        for (int i = 0; i < player.size(); i++) {
            tmp = player.get(i).nickname;
            if (!done.contains(tmp)) {
                remainingPlayers++;
            }
        }
        if (remainingPlayers < 2) {
            done.add(tmp);
            sendToAllPlayer(ServerMessages.newMatchDoneAction(done));
        }
    }

}
