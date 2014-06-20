package maedn_server.messages.client;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import maedn_server.messages.Response;

public class ClientMessages {

    public static Type getJoinType() {
        return new TypeToken<Response<Join>>() {
        }.getType();
    }
    
    public static Type getCreateType() {
        return new TypeToken<Response<Create>>() {
        }.getType();
    }
    
    public static Type getMoveType() {
        return new TypeToken<Response<Move>>() {
        }.getType();
    }
}
