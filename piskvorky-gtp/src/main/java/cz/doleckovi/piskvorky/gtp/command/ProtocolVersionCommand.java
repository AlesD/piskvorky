package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

public class ProtocolVersionCommand implements GTPCommand {

    public static final String NAME = "protocol_version";
    public static final String DESCRIPTION = "Version of the GTP Protocol";

    @Override
    public String execute(CommandContext context) {
        return "2";
    }

}
