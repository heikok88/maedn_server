package maedn_server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import maedn_server.messages.server.MatchNode;
import maedn_server.messages.server.Matches;
import maedn_server.messages.Response;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

public class WebSocketClient extends Verticle {

    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient().setHost("localhost").setPort(8080);

        client.connectWebsocket("", new Handler<WebSocket>() {
            public void handle(WebSocket ws) {
                Gson gs = new Gson();
                
                ws.dataHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer data) {
                        System.out.println("Received: " + data);
                        Type fooType = new TypeToken<Response<Matches>>(){}.getType();
                        Response r = gs.fromJson(data.toString(), fooType);
                        System.out.println("Converted: " + r);
                    }
                });
                
                List<MatchNode> matches = new ArrayList<>();
                matches.add(new MatchNode(1, 42));
                matches.add(new MatchNode(2, 24));
                matches.add(new MatchNode(3, 124));
                Matches m = new Matches(matches);
                
                Response<Matches> rs = new Response<>("matches",m);
                
                System.out.println("Send:     " + gs.toJson(rs));
                //Send some data
                ws.writeTextFrame(gs.toJson(rs));
            }
        });
    }
    
    
}
