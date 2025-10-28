package cz.doleckovi.piskvorky.api.board;

public enum Direction {

	HORIZONTAL("\u21D2"),
	VERTICAL("\u21D3"),
	UPHILL("\u21D7"),
	DOWNHILL("\u21D8");

    Direction(String arrow) {
        this.arrow = arrow;
    }

    private String arrow;

    @Override
    public String toString() {
        return super.toString();
    }

}
