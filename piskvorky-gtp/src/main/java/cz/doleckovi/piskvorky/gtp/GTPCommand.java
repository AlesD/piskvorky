package cz.doleckovi.piskvorky.gtp;

public interface GTPCommand {
    String execute(CommandContext context);
}
