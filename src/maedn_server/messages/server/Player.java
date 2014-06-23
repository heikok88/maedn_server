package maedn_server.messages.server;

public class Player {

    public final String nickname;
    public final String color;
    public final boolean ready;

    public Player(String nickname, String color) {
        this(nickname, color, false);
    }

    public Player(String nickname, String color, boolean ready) {
        this.nickname = nickname;
        this.color = color;
        this.ready = ready;
    }
}
