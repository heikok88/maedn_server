package maedn_server.messages.server;

import java.util.List;

public class MatchDone {

    public final List<String> ranking;

    public MatchDone(List<String> ranking) {
        this.ranking = ranking;
    }
}
