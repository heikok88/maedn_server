package maedn_server.messages.server;

public class MatchNode {
    public final int id;
    public final int countMembers;
    
    public MatchNode(int id, int countMembers) {
        this.id = id;
        this.countMembers = countMembers;
    }
}
