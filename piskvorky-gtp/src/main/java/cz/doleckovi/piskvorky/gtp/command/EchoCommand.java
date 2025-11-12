package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;

public class EchoCommand implements GNUGoCommand {

    public static final String NAME = "echo";
    public static final String DESCRIPTION = "Echo the parameter";

    private final String text;

    public EchoCommand(String text) {
        this.text = text;
    }

    @Override
    public String execute(CommandContext context) {
        return text;
    }

}
