package maedn_server;

import org.vertx.java.platform.Verticle;

public class MaednServer extends Verticle {

    @Override
    public void start() {
        container.deployVerticle(WebSocket.class.getName());
    }
}
