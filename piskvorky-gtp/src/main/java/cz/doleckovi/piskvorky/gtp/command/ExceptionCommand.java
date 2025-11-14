package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandContext;

public class ExceptionCommand implements Command {

    private final RuntimeException exception;

    public ExceptionCommand(RuntimeException exception) {
        this.exception = exception;
    }

    @Override
    public String execute(CommandContext context) {
        throw exception;
    }

}
