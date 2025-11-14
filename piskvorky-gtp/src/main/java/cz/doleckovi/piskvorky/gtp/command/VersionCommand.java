package cz.doleckovi.piskvorky.gtp.command;

import cz.doleckovi.piskvorky.gtp.CommandContext;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class VersionCommand implements GTPCommand {

	private static final String VERSION_RESOURCE = "/META-INF/maven/cz.doleckovi.piskvorky/piskvorky-gtp/pom.properties";
	private static final Logger LOGGER = Logger.getLogger(CommandFactoryImpl.class.getSimpleName());

	public static final String NAME = "version";

    private final String version;

    public VersionCommand(String version) {
        this.version = version;
    }

	public VersionCommand() {
		var properties = new Properties();
		try (var is = VersionCommand.class.getResourceAsStream(VERSION_RESOURCE)) {
			if (is != null) properties.load(is);
		} catch (IOException e) {
			LOGGER.warning("Failed to load pom.properties");
		}
		this(properties.getProperty("version", "unknown"));
	}

    @Override
    public String execute(CommandContext context) {
        return version;
    }

}
