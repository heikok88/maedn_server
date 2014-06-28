package maedn_server;

import org.vertx.java.platform.Verticle;

public class MaednServer extends Verticle {

    @Override
    public void start() {
        container.deployVerticle(WebSocket.class.getName());
//        try {
//            for (int x = 0; x < 1; x++) {
//                Thread.sleep(500);
//                container.deployVerticle(WebSocketClient.class.getName());
//            }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(MaednServer.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
