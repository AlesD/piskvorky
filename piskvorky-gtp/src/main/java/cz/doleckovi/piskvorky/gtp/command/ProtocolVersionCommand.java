package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.action.Context;

@CommandInfo(name = ProtocolVersionCommand.NAME, description = "Report protocol version")
public class ProtocolVersionCommand implements Command {

	public static final String NAME = "protocol_version";
	public static final ProtocolVersionCommand INSTANCE = new ProtocolVersionCommand();

	private ProtocolVersionCommand() {}

	@Override
	public String execute(Context context) {
        return "2";
    }

	@Override
	public String toString() {
		return ProtocolVersionCommand.NAME;
	}

}
