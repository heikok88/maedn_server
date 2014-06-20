package maedn_server.messages.client;

public class Join {

    private final int matchId;
    private final String nickname;

    public Join(int matchId, String nickname) {
        this.matchId = matchId;
        this.nickname = nickname;
    }
}
