package cz.doleckovi.piskvorky.api.board;

public enum Direction {

	HORIZONTAL("⇒"),    // U+21D2
	VERTICAL("⇓"),      // U+21D3
	UPHILL("⇗"),        // U+21D7
	DOWNHILL("⇘");      // U+21D8

    Direction(String arrow) {
        this.arrow = arrow;
    }

    private final String arrow;

    @Override
    public String toString() {
        return arrow;
    }

}
