package maedn_server;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

public class BobVerticle extends Verticle {
	private static final Logger log = LoggerFactory.getLogger(BobVerticle.class);
	
	private static final int W = 5;
	private int ns = 0;
	private int nt = 0;
	
	@Override
	public void start() {
		send();
	}
	
	private void send() {
		while (nt - ns < W) {
			JsonObject p = new JsonObject();
			p.putNumber("s", nt);
			p.putString("msg", "Hello");
			++nt;
			sendMessage(p);
		}
	}
	
	private void sendMessage(JsonObject p) {
		vertx.eventBus().sendWithTimeout("alice", p, 5000, r -> {
			if (r.failed()) {
				log.info("Timeout " + p.getNumber("s"));
				sendMessage(p);
			} else {
				log.info("Ack " + p.getInteger("s"));
				++ns;
				send();
			}
		});
	}
}

