package maedn_server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.vertx.java.platform.Verticle;

public class MaednServer extends Verticle {

    @Override
    public void start() {
        container.deployVerticle(WebSocket.class.getName());
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(MaednServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        container.deployVerticle(WebSocketClient.class.getName());
    }
}
