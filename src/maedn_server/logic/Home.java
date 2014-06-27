package maedn_server.logic;

import java.util.Arrays;

public class Home extends Fields {

    private static final int FIELDS = 4;

    public Home(int id) {
        super(FIELDS);
        init(id);
    }

    private void init(int id) {
        switch (id) {
            case 0:
                addPair(0, Arrays.asList(1, 5));
                addPair(1, Arrays.asList(2, 5));
                addPair(2, Arrays.asList(3, 5));
                addPair(3, Arrays.asList(4, 5));
                break;
            case 1:
                addPair(0, Arrays.asList(5, 1));
                addPair(1, Arrays.asList(5, 2));
                addPair(2, Arrays.asList(5, 3));
                addPair(3, Arrays.asList(5, 4));
                break;
            case 2:
                addPair(0, Arrays.asList(9, 5));
                addPair(1, Arrays.asList(8, 5));
                addPair(2, Arrays.asList(7, 5));
                addPair(3, Arrays.asList(6, 5));
                break;
            case 3:
                addPair(0, Arrays.asList(5, 9));
                addPair(1, Arrays.asList(5, 8));
                addPair(2, Arrays.asList(5, 7));
                addPair(3, Arrays.asList(5, 6));
                break;
            default:
                throw new IllegalArgumentException(
                        "Only values between 0 and 3 allowed");
        }
    }

}
