package cz.doleckovi.piskvorky.gtp;

public interface Command {
    String execute(CommandContext context);
}
