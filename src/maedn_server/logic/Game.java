package maedn_server.logic;

import maedn_server.Client;
import maedn_server.messages.Action;
import maedn_server.messages.Response;

public class Game extends WebsocketReceiver {

    @Override
    public void reveiceData(Client client, String json) {
        if (isAction(json)) {
            Action action = gson.fromJson(json, Action.class);
            switch (action.action) {
                default:
                    // TODO: handle forbidden json object
            }
        } else if (isResponse(json)) {
            // TODO
            Response response = gson.fromJson(json, Response.class);
            switch (response.response) {
                default:
                    // TODO: handle forbidden json object
            }
        }
    }

}
