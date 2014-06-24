package maedn_server.logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import maedn_server.Client;

public abstract class WebsocketReceiver {

    protected Gson gson = new Gson();
    
    public abstract void reveiceData(Client client, String json);

    private boolean isMsgType(String json, String msgType) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(json, type);
        return (map.get(msgType) != null);
    }

    protected boolean isAction(String json) {
        return isMsgType(json, "action");
    }

    protected boolean isResponse(String json) {
        return isMsgType(json, "response");
    }

}
