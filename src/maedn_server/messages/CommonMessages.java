package maedn_server.messages;

public class CommonMessages {

    public static Action newSimpleAction(String action) {
        return new Action(action);
    }

    public static Response newSimpleResponse(String response) {
        return new Response(response);
    }

    public static Action<TextMsg> newMsgAction(String action, String msg) {
        return new Action<>(action, new TextMsg(msg));
    }

    public static Response<TextMsg> newMsgResponse(String response, String msg) {
        return new Response<>(response, new TextMsg(msg));
    }

}
