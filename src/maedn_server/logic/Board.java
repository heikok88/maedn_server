package maedn_server.logic;

import java.util.Arrays;

public class Board extends Fields{

    public Board(int cnt) {
        super(cnt);
        
        addPair(0, Arrays.asList(4, 0));
        addPair(1, Arrays.asList(4, 1));
        addPair(2, Arrays.asList(4, 2));
        addPair(3, Arrays.asList(4, 3));
        addPair(4, Arrays.asList(4, 4));
        addPair(5, Arrays.asList(3, 4));
        addPair(6, Arrays.asList(2, 4));
        addPair(7, Arrays.asList(1, 4));
        addPair(8, Arrays.asList(0, 4));
        addPair(9, Arrays.asList(0, 5));
        
        addPair(10, Arrays.asList(0, 6));
        addPair(11, Arrays.asList(1, 6));
        addPair(12, Arrays.asList(2, 6));
        addPair(13, Arrays.asList(3, 6));
        addPair(14, Arrays.asList(4, 6));
        addPair(15, Arrays.asList(4, 7));
        addPair(16, Arrays.asList(4, 8));
        addPair(17, Arrays.asList(4, 9));
        addPair(18, Arrays.asList(4, 10));
        addPair(19, Arrays.asList(5, 10));
        
        addPair(20, Arrays.asList(6, 10));
        addPair(21, Arrays.asList(6, 9));
        addPair(22, Arrays.asList(6, 8));
        addPair(23, Arrays.asList(6, 7));
        addPair(24, Arrays.asList(6, 6));
        addPair(25, Arrays.asList(7, 6));
        addPair(26, Arrays.asList(8, 6));
        addPair(27, Arrays.asList(9, 6));
        addPair(28, Arrays.asList(10, 6));
        addPair(29, Arrays.asList(10, 5));
        
        addPair(30, Arrays.asList(10, 4));
        addPair(31, Arrays.asList(9, 4));
        addPair(32, Arrays.asList(8, 4));
        addPair(33, Arrays.asList(7, 4));
        addPair(34, Arrays.asList(6, 4));
        addPair(35, Arrays.asList(6, 3));
        addPair(36, Arrays.asList(6, 2));
        addPair(37, Arrays.asList(6, 1));
        addPair(38, Arrays.asList(6, 0));
        addPair(39, Arrays.asList(5, 0));
    }



}
