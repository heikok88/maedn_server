package maedn_server.messages.client;

public class Move {
    private final int fromX;
    private final int fromY;
    private final int toX;
    private final int toY;
    
    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }
}
