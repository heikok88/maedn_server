package maedn_server.messages;

public class Action<T> {
    private final String action;
    private final T payload;
    
    public Action(String action) {
        this(action, null);
    }
    
    public Action(String action, T payload) {
        this.action = action;
        this.payload = payload;
    }
}
