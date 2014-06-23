package maedn_server;

import maedn_server.logic.IState;
import org.vertx.java.core.http.ServerWebSocket;

public class Client  {

    private IState state;
    private final ServerWebSocket ws;

    public Client(IState state, ServerWebSocket ws) {
        this.state = state;
        this.ws = ws;
    }

    public void receiveData(String data) {
        System.out.println("Send: " + data); // TODO: remove, only for debugging
        state.reveiceData(this, data);
    }
    
    public void sendData(String data) {
        ws.writeTextFrame(data);
    }
    
    public void setLogic(IState state) {
        this.state = state;
    }
    
}