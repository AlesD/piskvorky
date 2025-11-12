package cz.doleckovi.piskvorky.gtp;

public interface Command {
    void execute(CommandContext context);
}
