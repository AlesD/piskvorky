package cz.doleckovi.piskvorky.gtp;

import cz.doleckovi.piskvorky.api.position.Stone;

public class Move {

    private final Stone stone;
    private final Vertex vertex;

    public Move(Stone stone, Vertex vertex) {
        this.stone = stone;
        this.vertex = vertex;
    }
}
