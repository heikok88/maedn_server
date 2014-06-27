package maedn_server.logic;

import java.util.Arrays;
import maedn_server.messages.server.Figure;

public class Seat extends Fields {

    private static final int SEATS = 4;

    public Seat(int id) {
        super(SEATS);
        init(id);
    }

    private void init(int id) {
        int x, y;
        switch (id) {
            case 0:
                x = 0;
                y = 0;
                break;
            case 1:
                x = 0;
                y = 9;
                break;
            case 2:
                x = 9;
                y = 9;
                break;
            case 3:
                x = 9;
                y = 0;
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
                figures[i] = figure;
                break;
            }
        }
    }

}
