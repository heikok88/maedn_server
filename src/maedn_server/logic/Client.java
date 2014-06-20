package maedn_server.logic;

import org.vertx.java.core.http.ServerWebSocket;

public class Client {
    private final ServerWebSocket ws;
    private final ICallback cl;
    private String nickname;
    
    public Client(ServerWebSocket ws, ICallback cl) {
        this.ws = ws;
        this.cl = cl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
