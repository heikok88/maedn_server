package maedn_server.messages.server;

public class Client {

    private final String nickname;
    private final String color;
    private final boolean ready;

    public Client(String nickname, String color) {
        this(nickname, color, false);
    }

    public Client(String nickname, String color, boolean ready) {
        this.nickname = nickname;
        this.color = color;
        this.ready = ready;
    }
}
