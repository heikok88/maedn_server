package maedn_server;

import maedn_server.logic.Client;
import maedn_server.logic.Foyer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.platform.Verticle;

public class WebSocket extends Verticle {

    private final int PORT = 8181;

    @Override
    public void start() {
        Foyer foyer = new Foyer();
        
        HttpServer server = vertx.createHttpServer();
        System.out.println("Server is listening on port " + PORT);

        server.websocketHandler(new Client(foyer)).listen(PORT, "localhost");
    }
}
