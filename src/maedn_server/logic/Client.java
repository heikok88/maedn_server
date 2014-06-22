package maedn_server.logic;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.ServerWebSocket;

public class Client implements Handler<ServerWebSocket> {

    private IState state;
    private String nickname = "";
    private ServerWebSocket ws;

    public Client(IState state) {
        this.state = state;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    private void receiveData(Buffer data) {
        state.reveiceData(this, data.toString());
    }
    
    public void sendData(String data) {
        ws.writeTextFrame(data);
    }
    
    public void setLogic(IState state) {
        this.state = state;
    }

    @Override
    public void handle(ServerWebSocket ws) {
        this.ws = ws;
        ws.dataHandler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer data) {
                receiveData(data);
            }
        });
    }

}
