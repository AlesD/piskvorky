package cz.doleckovi.piskvorky.gtp.command;

@CommandInfo(name = "protocol_version", description = "Report protocol version")
public class ProtocolVersionCommand implements Command {

	public static final ProtocolVersionCommand INSTANCE = new ProtocolVersionCommand();

	private ProtocolVersionCommand() {}

	@Override
	public String execute() {
        return "2";
    }

}
