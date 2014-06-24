package maedn_server;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import maedn_server.messages.Action;
import maedn_server.messages.CommonMessages;
import maedn_server.messages.client.Create;
import maedn_server.messages.client.Join;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

public class WebSocketClient extends Verticle {

    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient().setHost("localhost").setPort(8181);

        client.connectWebsocket("", new Handler<WebSocket>() {
            public void handle(WebSocket ws) {
                Gson gs = new Gson();

                ws.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer data) {
                        System.out.println("Received:  " + data);
                    }
                });

                Action rs;
//                rs = CommonMessages.newSimpleAction("connect");
//                ws.writeTextFrame(gs.toJson(rs));

                //                rs = CommonMessages.newSimpleAction("getMatches");
                //                ws.writeTextFrame(gs.toJson(rs));
                
                rs = new Action<Join>("join", new Join(0, "heiko"));
                ws.writeTextFrame(gs.toJson(rs));
            }
        });
    }

}
