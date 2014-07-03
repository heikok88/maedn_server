package maedn_server.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;

import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.server.Dice;
import maedn_server.messages.server.GameParticipants;
import maedn_server.messages.server.MatchDone;
import maedn_server.messages.server.MatchUpdate;
import maedn_server.messages.server.PlayerDone;
import maedn_server.messages.server.TimerStart;

public class ResponseManager {

    private static final long timeout = 1000L;
    private final WebsocketReceiver wsr;
    HashSet<Tasks> Task = new HashSet<Tasks>();

    public ResponseManager(WebsocketReceiver wsr) {
        this.wsr = wsr;
    }

    public boolean attendResponse(Client client, String responseMSG) {
        boolean isClient = false;
        Iterator it = Task.iterator();

        while (it.hasNext()) {
            Tasks task = (Tasks) it.next();
            if (task.getClient() == client) {
                if (task.getResponse().equals(responseMSG)) {
                    isClient = true;
                    task.getTimer().cancel();
                    Task.remove(task);
                    break;
                }
            }
        }
       return isClient;
    }

    public void addTask(Client client, Action action) {
        Timer timer = new Timer();
        Tasks newTask = null;
        
        if ((action.payload instanceof TimerStart)) {
            newTask = new Tasks(client, "timerStarted", timer);
        } else if ((action.payload instanceof GameParticipants)) {
            newTask = new Tasks(client, "playersUpdated", timer);
        } else if (action.payload == null) {
            if (action.action.equals("matchStart")) {
                newTask = new Tasks(client, "matchStarted", timer);
            } else if (action.action.equals("timerAbort")) {
                newTask = new Tasks(client, "timerAborted", timer);
            }
        } else if (action.payload instanceof MatchUpdate) {
            newTask = new Tasks(client, "matchUpdated", timer);
        } else if (action.payload instanceof Dice) {
            newTask = new Tasks(client, "diceRolled", timer);
        } else if (action.payload instanceof PlayerDone) {
            newTask = new Tasks(client, "playerDone", timer);
        } else if (action.payload instanceof MatchDone) {
            newTask = new Tasks(client, "matchDone", timer);
        }

        Task.add(newTask);
        if (newTask == null) {
            System.out.println("Something went wrong!");
        }
        timer.schedule(newTask, timeout);
    }

    public void stopTaskFromClient(Client client) {
        Iterator it = Task.iterator();

        while (it.hasNext()) {
            Tasks task = (Tasks) it.next();
            if (task.getClient() == client) {
                task.getTimer().cancel();
                task.getTimer().purge();
                Task.remove(task);
              }
        }
    }

    //inner Class
    private class Tasks extends java.util.TimerTask {

        private final Client client;
        private final String responseMSG;
        private final Timer timer;

        public Tasks(Client client, String responseMSG, Timer timer) {
            this.client = client;
            this.responseMSG = responseMSG;
            this.timer = timer;
         //   System.out.println("Tasks with Response: " +responseMSG);
        }

        public Client getClient() {
            return this.client;
        }

        public Timer getTimer() {
            return this.timer;
        }

        public String getResponse() {
            return this.responseMSG;
        }

        @Override
        public void run() {
            System.out.println("Client: " + client.me + " waiting on Response " + this.responseMSG + " with timeout");
            client.getReceiver().removeClient(client);
        }

    }
}
