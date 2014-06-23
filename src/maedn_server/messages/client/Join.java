package maedn_server.messages.client;

public class Join {

    public final int matchId;
    public final String nickname;

    public Join(int matchId, String nickname) {
        this.matchId = matchId;
        this.nickname = nickname;
    }
}
