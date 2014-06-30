package maedn_server.logic;

import java.util.Enumeration;
import java.util.Stack;
import java.util.Timer;
import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.Response;
import maedn_server.messages.server.GameParticipants;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.Player;
import maedn_server.messages.server.ServerMessages;

public class Room extends WebsocketReceiver {

    private final static String[] colors = {"red", "blue", "green", "yellow"};
    private final int id;
    private final Stack<Client> clients;
    private final Stack<Player> player;
    private final int TIME = 10;
    private Timer timer;
    private int lastCnt = 0;

    public Room(int id) {
        this.id = id;
        this.clients = new Stack<>();
        this.player = new Stack<>();
    }

    private class timerTask extends java.util.TimerTask {

        @Override
        public void run() {
            startGame();
        }
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
            client.setReceiver(this);
            player.push(new Player(nickname));
            setColors();
            timerLogic();
        }
        return ret;
    }

    private void removeClient(Client client) {
        int index = clients.indexOf(client);
        if (index != -1) {
            clients.remove(index);
            player.remove(index);
            Foyer.getFoyerInstance().registerClient(client, true);
            if (clients.size() > 0) {
                setColors();
                notifyAllPlayer();
                timerLogic();
            } else {
                Foyer.getFoyerInstance().removeRoom(this);
            }

        }
    }

    public void notifyJoinedPlayer(Client client) {
        Response<GameParticipants> rs = ServerMessages.newJoinedResponse(
                id, player);
        client.sendData(gson.toJson(rs));
    }

    public void notifyPlayer(Client client) {
        Response<GameParticipants> rs = ServerMessages.newUpdatePlayersResponse(
                id, player);
        client.sendData(gson.toJson(rs));
    }

    public void notifyAllPlayer() {
        notifyAllPlayer(null);
    }

    public void notifyAllPlayer(Client client) {
        Action<GameParticipants> ac = ServerMessages.newUpdatePlayersAction(
                id, player);
        sendToAllPlayer(ac, client);
    }

    private void sendToAllPlayer(Action o) {
        sendToAllPlayer(o, null);
    }

    private void sendToAllPlayer(Action o, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            Client cl = clients.get(i);
            if (cl != client) {
                cl.sendData(gson.toJson(o));
            }
        }
    }

//    private List<Player> getPlayersList() {
//        List<Player> l = new LinkedList<>();
//        Enumeration<Player> p = player.elements();
//        while (p.hasMoreElements()) {
//            l.add(p.nextElement());
//        }
//        return l;
//    }

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
        this.removeClient(client);
        client.sendData(gson.toJson(CommonMessages.newSimpleResponse("left")));
    }

    private void handleReady(Client client) {
        int index = clients.indexOf(client);
        if (index != -1) {
            player.get(index).ready = true;
            notifyPlayer(client);
            notifyAllPlayer(client);
            timerLogic();
        }
    }

    private void setColors() {
        int cnt = 0;
        Enumeration<Player> p = player.elements();
        while (p.hasMoreElements()) {
            p.nextElement().color = colors[cnt++];
        }
    }

    private void timerLogic() {
        if (lastCnt < clients.size()) {
            stopTimer();
        } else {
            if (lastCnt > clients.size()) {
                stopTimer();
            }
            if (canStartTimer()) {
                startTimer();
            }
        }
        lastCnt = clients.size();
    }

    private boolean canStartTimer() {
        boolean bool = false;
        if (clients.size() >= 2) {
            bool = true;
            Enumeration<Player> p = player.elements();
            while (p.hasMoreElements()) {
                if (!p.nextElement().ready) {
                    bool = false;
                    break;
                }
            }
        }
        return bool;
    }

    private void startTimer() {
        timer = new Timer(true);
        timer.schedule(new timerTask(), (long) (TIME * 1000));
        sendToAllPlayer(ServerMessages.newTimerStartAction(TIME));
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            sendToAllPlayer(CommonMessages.newSimpleAction("timerAbort"));
        }
    }

    private void startGame() {
        timer.cancel();
        timer = null;
        System.out.println("Game started"); // TODO
        Foyer.getFoyerInstance().removeRoom(this);
        new Game(id, clients, player);
    }
}
