package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.execution.CommandExecutorService;
import cz.doleckovi.piskvorky.gtp.command.execution.InputProcessor;
import cz.doleckovi.piskvorky.gtp.parser.AntlrCommandParser;
import cz.doleckovi.piskvorky.gtp.parser.GTPParser;
import cz.doleckovi.piskvorky.gtp.parser.ParserListener;
import cz.doleckovi.piskvorky.gtp.parser.ScannerLineSupplier;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorCompletionService;

@AutoConfiguration
@Profile("gtp")
@ConditionalOnBooleanProperty(name = "piskvorky.gtp.enabled", matchIfMissing = true)
public class GTPAutoConfiguration {

	@Configuration
	@Import({CommandConfiguration.class, ParserConfiguration.class, CommandExecutionConfiguration.class})
	@EnableConfigurationProperties(GTPProperties.class)
	public static class GTPConfiguration {

		private static final Logger LOG = LoggerFactory.getLogger(GTPAutoConfiguration.class);

	}

}
