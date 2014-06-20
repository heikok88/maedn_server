package example;

import java.util.TreeMap;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

public class AliceVerticle extends Verticle {
	private static final Logger log = LoggerFactory.getLogger(BobVerticle.class);
	
	private static final int W = 5;
	private int nr = 0;
	private TreeMap<Integer, JsonObject> buf = new TreeMap<>();
	
	@Override
	public void start() {
		vertx.eventBus().registerHandler("alice", (Message<JsonObject> m) -> {
			/*if (Math.random() > 0.999) {
				return;
			}*/
			
			int s = m.body().getInteger("s");
			if (s == nr) {
				log.info(m.body().getString("msg"));
				m.reply(makeAck(s));
				++nr;
			} else if (s < nr || buf.containsKey(s)) {
				m.reply(makeAck(s));
			} else if (buf.size() < W) {
				buf.put(s, m.body());
				m.reply(makeAck(s));
			}
			
			while (buf.size() > 0 && buf.firstKey() == nr) {
				log.info(buf.remove(nr).getString("msg"));
				++nr;
			}
		});
	}
	
	private JsonObject makeAck(int s) {
		JsonObject ack = new JsonObject();
		ack.putNumber("s", s);
		return ack;
	}
}

