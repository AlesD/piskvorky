package cz.doleckovi.piskvorky.gtp.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("piskvorky.gtp")
public class GTPProperties {

	private boolean enabled = false;
	private Resource scanner;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Resource getScanner() {
		return scanner;
	}

	public void setScanner(Resource scanner) {
		this.scanner = scanner;
	}
}
