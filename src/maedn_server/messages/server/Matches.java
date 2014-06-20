package maedn_server.messages.server;

import java.util.List;

public class Matches {

    private final List<MatchNode> matches;

    public Matches(List<MatchNode> matches) {
        this.matches = matches;
    }
}
