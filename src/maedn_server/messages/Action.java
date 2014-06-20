package maedn_server.messages;

public class Action {
    private final String action;
    private final Object payload;
    
    public Action(String action) {
        this(action, null);
    }
    
    public Action(String action, Object payload) {
        this.action = action;
        this.payload = payload;
    }
}
