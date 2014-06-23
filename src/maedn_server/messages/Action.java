package maedn_server.messages;

public class Action<T> {
    public final String action;
    public final T payload;
    
    public Action(String action) {
        this(action, null);
    }
    
    public Action(String action, T payload) {
        this.action = action;
        this.payload = payload;
    }
}
