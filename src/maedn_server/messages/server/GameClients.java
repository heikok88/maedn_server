package maedn_server.messages.server;

import java.util.List;

public class GameClients {

    private final int matchId;
    private final List<Client> clients;

    public GameClients(int matchId, List<Client> clients) {
        this.matchId = matchId;
        this.clients = clients;
    }
}
