package maedn_server.messages.server;

import java.util.List;

public class GameParticipants {

    private final int matchId;
    private final List<Player> clients;

    public GameParticipants(int matchId, List<Player> clients) {
        this.matchId = matchId;
        this.clients = clients;
    }
}
