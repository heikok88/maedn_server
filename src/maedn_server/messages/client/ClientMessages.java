package maedn_server.messages.client;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import maedn_server.messages.Action;

public class ClientMessages {

    public static Type getJoinType() {
        return new TypeToken<Action<Join>>() {
        }.getType();
    }

    public static Type getCreateType() {
        return new TypeToken<Action<Create>>() {
        }.getType();
    }

    public static Type getMoveType() {
        return new TypeToken<Action<Move>>() {
        }.getType();
    }
}
