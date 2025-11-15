package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;

@CommandInfo(name = "echo", description = "Print ")
public class EchoCommand implements Command {

    public static final String NAME = "echo";

    private final String text;

    public EchoCommand(String text) {
        this.text = text;
    }

    @Override
    public String execute() {
        return text;
    }

}
