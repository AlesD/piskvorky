package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.command.Command;

import java.util.function.Consumer;

public interface CommandConsumer extends Consumer<Command> {}
