package maedn_server.logic;

import org.vertx.java.core.buffer.Buffer;

public interface ICallback {
    void callback(Client cl, Buffer data);
}
