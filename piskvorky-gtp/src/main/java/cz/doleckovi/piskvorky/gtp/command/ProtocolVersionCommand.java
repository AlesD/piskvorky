package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;
import cz.doleckovi.piskvorky.gtp.GTPCommand;

@Command(name = ProtocolVersionCommand.NAME, description = "Version of the GTP Protocol")
public class ProtocolVersionCommand implements GTPCommand {

	public static final String NAME = "protocol_version";
	public static final ProtocolVersionCommand INSTANCE = new ProtocolVersionCommand();

	private ProtocolVersionCommand() {}

	@Override
	public String execute(CommandContext context) {
        return "2";
    }

}
