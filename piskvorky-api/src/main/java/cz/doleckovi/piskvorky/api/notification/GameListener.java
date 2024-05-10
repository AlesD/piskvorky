package cz.doleckovi.piskvorky.api.notification;

import java.util.EventListener;

public interface GameListener extends EventListener {

	void movePlayed(MoveEvent e);
	void moveTakenBack(MoveEvent e);

}
