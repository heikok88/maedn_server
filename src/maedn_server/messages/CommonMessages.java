package maedn_server.messages;

public class CommonMessages {

    public static Action newSimpleAction(String action) {
        return new Action(action);
    }

    public static Response newSimpleResponse(String response) {
        return new Response(response);
    }

    private static TextMsg newTextMsg(String msg) {
        return new TextMsg(msg);
    }

    public static Action<TextMsg> newMsgAction(String action, String msg) {
        return new Action<TextMsg>(action, newTextMsg(msg));
    }

    public static Response<TextMsg> newMsgResponse(String response, String msg) {
        return new Response<TextMsg>(response, newTextMsg(msg));
    }

}
