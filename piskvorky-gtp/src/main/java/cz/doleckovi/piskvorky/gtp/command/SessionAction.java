package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Session;

public interface SessionAction {

	ScopedValue<Session> SESSION = ScopedValue.newInstance();

	default Session session() {
		return SESSION.get();
	}

}
