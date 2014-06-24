package maedn_server;

import maedn_server.logic.WebsocketReceiver;
import org.vertx.java.core.http.ServerWebSocket;

public class Client  {

    private WebsocketReceiver receiver;
    private final ServerWebSocket ws;

    public Client(WebsocketReceiver receiver, ServerWebSocket ws) {
        this.receiver = receiver;
        this.ws = ws;
    }

    public void receiveData(String data) {
        System.out.println("Send: " + data); // TODO: remove, only for debugging
        receiver.reveiceData(this, data);
    }
    
    public void sendData(String data) {
        ws.writeTextFrame(data);
    }
    
    public void setLogic(WebsocketReceiver receiver) {
        this.receiver = receiver;
    }
    
}
