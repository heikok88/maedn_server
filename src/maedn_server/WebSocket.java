package maedn_server;

import maedn_server.logic.Foyer;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.platform.Verticle;

public class WebSocket extends Verticle {

    private final int PORT = 8181;

    @Override
    public void start() {
        Foyer foyer = Foyer.getFoyerInstance();

        HttpServer server = vertx.createHttpServer();
        System.out.println("Server is listening on port " + PORT);

        server.websocketHandler(new Handler<ServerWebSocket>() {
            Client client;

            public void handle(ServerWebSocket ws) {
                client = new Client(ws);
                foyer.registerClient(client);

                ws.dataHandler(new Handler<Buffer>() {
                    public void handle(Buffer data) {
                        client.receiveData(data.toString());
                    }
                });
            }
        }).listen(PORT, "localhost");
    }
}
