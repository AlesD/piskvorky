package cz.doleckovi.piskvorky.api;

public interface Self<SELF extends Self<SELF>> {
	SELF self();
}
