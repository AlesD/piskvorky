package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.command.Command;

public interface CommandParser {

	Command parse(String line);

}
