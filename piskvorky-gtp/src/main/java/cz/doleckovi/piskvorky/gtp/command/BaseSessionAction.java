package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Session;

public class BaseSessionAction implements SessionAction {

	protected Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

}
