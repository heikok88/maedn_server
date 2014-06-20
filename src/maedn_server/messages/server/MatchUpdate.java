package maedn_server.messages.server;

import java.util.List;

public class MatchUpdate {

    private final String currentPlayerNickname;
    private final List<Figure> board;

    public MatchUpdate(String currentPlayerNickname, List<Figure> board) {
        this.currentPlayerNickname = currentPlayerNickname;
        this.board = board;
    }
}
