package maedn_server.messages.server;

import java.util.List;

public class Figure {

    public final String nickname;
    public int x;
    public int y;

    public Figure(String nickname) {
        this.nickname = nickname;
    }

    public void setXY(List<Integer> xy) {
        setXY(xy.get(0), xy.get(1));
    }
    
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
