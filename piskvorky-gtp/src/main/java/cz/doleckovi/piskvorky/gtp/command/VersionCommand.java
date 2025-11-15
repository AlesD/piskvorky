package cz.doleckovi.piskvorky.gtp.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@CommandInfo(name = "version", description = "Report the version number of the program")
public class VersionCommand implements Command {

	private static final String VERSION_RESOURCE = "/META-INF/maven/cz.doleckovi.piskvorky/piskvorky-gtp/pom.properties";
	private static final Logger LOG = LoggerFactory.getLogger(VersionCommand.class);

	private final String version;

	public VersionCommand(String version) {
        this.version = version;
    }

	public VersionCommand() {
		var properties = new Properties();
		try (var is = VersionCommand.class.getResourceAsStream(VERSION_RESOURCE)) {
			if (is != null) properties.load(is);
		} catch (IOException e) {
			LOG.warn("Failed to load pom.properties", e);
		}
		this(properties.getProperty("version", "unknown"));
	}

    @Override
    public String execute() {
        return version;
    }

}
