package maedn_server.messages.server;

public class MatchNode {
    private final int id;
    private final int countMembers;
    
    public MatchNode(int id, int countMembers) {
        this.id = id;
        this.countMembers = countMembers;
    }
}
