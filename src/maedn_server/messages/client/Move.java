package maedn_server.messages.client;

public class Move {
    public final int fromX;
    public final int fromY;
    public final int toX;
    public final int toY;
    
    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }
}
