package maedn_server;

import org.vertx.java.platform.Verticle;

public class MainVerticle extends Verticle {
	@Override
	public void start() {
		container.deployVerticle(AliceVerticle.class.getName());
		container.deployVerticle(BobVerticle.class.getName());
	}
}
