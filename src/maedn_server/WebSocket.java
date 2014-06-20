package maedn_server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

public class WebSocket extends Verticle {

    private final int PORT = 8080;
    
    @Override
    public void start() {
        System.out.println("Server is listening on port " + PORT);
        
        HttpServer server = vertx.createHttpServer();

        server.websocketHandler(new Handler<ServerWebSocket>() {
            public void handle(ServerWebSocket ws) {
                System.out.println("Connection established");
            }
        }).listen(PORT, "localhost");
    }
}
