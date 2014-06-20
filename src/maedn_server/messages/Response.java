package maedn_server.messages;

public class Response<T> {
    public final String response;
    public final T payload;
    
    public Response(String response) {
        this(response, null);
    }
    
    public Response(String response, T payload) {
        this.response = response;
        this.payload = payload;
    }
}
