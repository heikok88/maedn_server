package maedn_server;

import java.util.Timer;
import java.util.TimerTask;
import maedn_server.logic.WebsocketReceiver;
import org.vertx.java.core.http.ServerWebSocket;

public class Client {

    public final int me;
    private static int cnt = 0; // TODO : Only for debugging
    private WebsocketReceiver receiver;
    private final ServerWebSocket ws;
    private Timer timer = null;
    private PlayerTimeOut scheduledTask = null;
    private static final long timeout = (1000L * 120) ;

    public Client(ServerWebSocket ws) {
        this.ws = ws;
        me = cnt++; // TODO : Only for debugging
    }

    public void receiveData(String data) {
        System.out.println("Client send (" + me + "): " + data); // TODO: remove, only for debugging
        if (receiver != null) {
            receiver.reveiceData(this, data);
        }
    }

     public void sendData(String data) {
        try {
            ws.writeTextFrame(data);
            System.out.println("Server send (" + me + "): " + data); // TODO: remove, only for debugging
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public void setReceiver(WebsocketReceiver receiver) {
        this.receiver = receiver;
    }

    public void startTimer() {
        this.scheduledTask = new PlayerTimeOut(this);
        timer = new Timer();
        System.out.println("--------------------------------------------------------------------------------------------");
        System.out.println("Client: " + this.me + " start Timer " + this.timer + " with Task: " + this.scheduledTask);
        System.out.println("--------------------------------------------------------------------------------------------");
        this.timer.schedule(scheduledTask, timeout);
    }

    public void restartTimer() {
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Client: " + this.me + " stop Timer " + this.timer + " with Task: " + this.scheduledTask);
        System.out.println("Rest Time: " + (System.currentTimeMillis() - scheduledTask.scheduledExecutionTime()));
        System.out.println("---------------------------------------------------------------------------------------------");
        timer.cancel();
        startTimer();
    }

    public void stopTimer() {
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Client: " + this.me + " stop Timer " + this.timer + " with Task: " + this.scheduledTask);
        System.out.println("---------------------------------------------------------------------------------------------");
        if (timer != null) {
            this.timer.cancel();
        }
    }

    public Timer getTimer() {
        return timer;
    }

    public WebsocketReceiver getReceiver() {
        return this.receiver;
    }
}
