package cz.doleckovi.piskvorky.api.ui;

/** Information about {@link Game}. */
public interface GameInfo {

	/** Gets information about player playing with white stones. */
	PlayerInfo getWhite();

	/** Gets information about player playing with black stones. */
	PlayerInfo getBlack();

}
