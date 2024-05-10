package cz.doleckovi.piskvorky.api.game.notification;

import java.util.EventListener;

/** Listener for game events. */
public interface GameListener extends EventListener {

	/** Notification about move played.
	 * @param event Information about game and move
	 */
	void movePlayed(MoveEvent event);

	/** Notification about move taken back.
	 * @param event Information about game and move
	 */
	void moveTakenBack(MoveEvent event);

}
