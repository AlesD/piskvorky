package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionCommand implements GTPCommand {

    public static final String NAME = "version";
    public static final String DESCRIPTION = "Version of the engine";

    private final String version;

    public VersionCommand(String version) {
        this.version = version;
    }

    @Override
    public String execute(CommandContext context) {
        return version;
    }

}
