package maedn_server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

public class WebSocketClient extends Verticle {

    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient().setHost("localhost:8080");

        client.connectWebsocket("", new Handler<WebSocket>() {
            public void handle(WebSocket ws) {
                System.out.println("Client connected");
            }
        });
    }
}
