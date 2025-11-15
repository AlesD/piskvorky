package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.CommandFactoryImpl;
import cz.doleckovi.piskvorky.gtp.parser.CommandReader;
import cz.doleckovi.piskvorky.gtp.parser.CommandSupplier;
import cz.doleckovi.piskvorky.gtp.parser.LineSupplier;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Scanner;

@AutoConfiguration
@ConditionalOnBooleanProperty(name = "piskvorky.gtp.enabled", matchIfMissing = false)
public class GTPAutoConfiguration {

	@Configuration
	@EnableConfigurationProperties(GTPProperties.class)
	public static class GTPConfiguration {

		@ConditionalOnMissingBean(LineSupplier.class)
		public LineSupplier scaner(GTPProperties properties) throws IOException {
			Scanner scanner;
			if (properties.getScanner() != null) {
				var resource = properties.getScanner();
				if (resource.isFile()) {
					scanner = new Scanner(resource.getFile());
				} else {
					scanner = new Scanner(resource.getInputStream());
				}
			} else if (System.console() != null) {
				scanner = new Scanner(System.console().reader());
			} else {
				scanner = new Scanner(System.in, System.getProperty("stdin.encoding"));
			}
			return scanner::nextLine;
		}

		@ConditionalOnMissingBean(CommandFactory.class)
		public CommandFactoryImpl commandFactory(ListableBeanFactory beanFactory) {
			return new CommandFactoryImpl(beanFactory);
		}

		@ConditionalOnMissingBean(CommandSupplier.class)
		public CommandReader commandReader(LineSupplier lineSupplier, CommandFactory commandFactory) {
			return new CommandReader(lineSupplier, commandFactory);
		}

	}

}
