package maedn_server;

import maedn_server.logic.IState;
import org.vertx.java.core.http.ServerWebSocket;

public class Client  {

    private IState state;
    private String nickname = "";
    private final ServerWebSocket ws;

    public Client(IState state, ServerWebSocket ws) {
        this.state = state;
        this.ws = ws;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void receiveData(String data) {
        state.reveiceData(this, data);
    }
    
    public void sendData(String data) {
        ws.writeTextFrame(data);
    }
    
    public void setLogic(IState state) {
        this.state = state;
    }
    
}
