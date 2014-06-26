package maedn_server;

import maedn_server.logic.WebsocketReceiver;
import org.vertx.java.core.http.ServerWebSocket;

public class Client {

    private WebsocketReceiver receiver;
    private final ServerWebSocket ws;

    public Client(ServerWebSocket ws) {
        this.ws = ws;
    }

    public void receiveData(String data) {
        System.out.println("Client send: " + data); // TODO: remove, only for debugging
        if (receiver != null) {
            receiver.reveiceData(this, data);
        }
    }

    public void sendData(String data) {
        System.out.println("Server send: " + data); // TODO: remove, only for debugging
        ws.writeTextFrame(data);
    }

    public void setReceiver(WebsocketReceiver receiver) {
        this.receiver = receiver;
    }

}
