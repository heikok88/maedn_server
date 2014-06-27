package maedn_server.logic.luts;

import java.util.Arrays;
import maedn_server.messages.server.Figure;

public class Start extends Fields {

    private static final int SEATS = 4;

    public Start(int id, String nickname) {
        super(SEATS);
        init(id);
        for (int i = 0; i < SEATS; i++) {
            setFigure(i, new Figure(nickname));
        }
    }

    private void init(int id) {
        int x, y;
        switch (id) {
            case 0:
                x = 0;
                y = 0;
                break;
            case 1:
                x = 9;
                y = 0;
                break;
            case 2:
                x = 9;
                y = 9;
                break;
            case 3:
                x = 0;
                y = 9;
                break;
            default:
                throw new IllegalArgumentException(
                        "Only values between 0 and 3 allowed");
        }
        addPairs(x, y);
    }

    private void addPairs(int x, int y) {
        addPair(0, Arrays.asList(x, y));
        addPair(1, Arrays.asList(x + 1, y));
        addPair(2, Arrays.asList(x + 1, y + 1));
        addPair(3, Arrays.asList(x, y + 1));
    }
    
    public void setFigure(Figure figure) {
        for (int i = 0; i < figures.length; i++) {
            if (figures[i] == null) {
                setFigure(i, figure);
                break;
            }
        }
    }
    
    public Figure getFigure() {
        Figure f = null;
        for (int i = 0; i < figures.length; i++) {
            if (figures[i] != null) {
                f = figures[i];
                delFigure(i);
                break;
            }
        }
        return f;
    }
}
