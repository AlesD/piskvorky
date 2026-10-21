package cz.doleckovi.piskvorky.api.game;

import cz.doleckovi.piskvorky.api.position.Stone;

/** Symbolic constants for side to play. */
public enum Side {

    /** Side playing with white stones. */
    WHITE(Stone.WHITE),
    /** Side playing with black stones. */
    BLACK(Stone.BLACK);

    /** Stone used by the side. */
    public final Stone stone;

    Side(Stone stone) {
        this.stone = stone;
    }

    /** Gets opposite side. */
    public Side opposite() {
        return switch (this) {
            case WHITE -> BLACK;
            case BLACK -> WHITE;
        };
    }

}
