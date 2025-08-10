package cz.doleckovi.piskvorky.core.board;

import cz.doleckovi.piskvorky.api.board.Stone;

public class BoardTestUtils {

    public static Line line(String line) {
        var result = new LineImpl(line.length());
        for (int offset = result.length() - 1; offset >= 0; --offset)
            result = result.withStone(offset, Stone.valueOf(line.charAt(offset)));
        return result;
    }

    public static Line line(Stone[] stones) {
        var result = new LineImpl(stones.length);
        for (int offset = result.length() - 1; offset >= 0; --offset)
            result = result.withStone(offset, stones[offset]);
        return result;
    }
}
