package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.api.game.Side;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;
import cz.doleckovi.piskvorky.gtp.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.*;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(CommandFactory.class)
public class CommandConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(CommandConfiguration.class);

    @Autowired
    ListableBeanFactory beanFactory;

    /** Map of command names to bean names. */
    private Map<String, String> commandNames;

	private Map<String, String> getCommandNames() {
		if (this.commandNames == null) {
			LOG.debug("Collecting command names");
			var commandNames = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
			for (var beanName : beanFactory.getBeanNamesForType(Command.class, true, false)) {
				var beanClass = beanFactory.getType(beanName);
				var commandInfo = beanFactory.findAnnotationOnBean(beanName, CommandInfo.class);
				if (commandInfo == null) {
					LOG.debug("Bean {} ({}) implements unnamed command", beanName, beanClass.getSimpleName());
				} else {
					LOG.debug("Bean {} ({}) provides command {}", beanName, beanClass.getSimpleName(), commandInfo.name());
					commandNames.put(commandInfo.name(), beanName);
				}
			}
			this.commandNames = commandNames;
		}
		return commandNames;
	}


	@Bean
	public CommandFactoryImpl commandFactory() {
		return new CommandFactoryImpl(beanFactory, getCommandNames());
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public BoardSizeCommand boardSizeCommand(int size) {
		return new BoardSizeCommand(size);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ColorCommand colorCommand(Vertex vertex) {
		return new ColorCommand(vertex);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public EchoCommand echoCommand(String text) {
		return new EchoCommand(text);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public GoGuiSetupCommand goGuiSetupCommand(List<Move> moves) {
		return new GoGuiSetupCommand(moves);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public KnownCommandCommand knownCommandCommand(String commandName) {
		return new KnownCommandCommand(commandName, getCommandNames().containsKey(commandName));
	}

	@Bean
	public ListCommandsCommand listCommandsCommand() {
		return new ListCommandsCommand(List.copyOf(getCommandNames().keySet()));
	}

	@Bean
	public NameCommand nameCommand(@Value("${spring.application.name:piskvorky}") String name) {
		return new NameCommand(name);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public PiskvorkyConfigCommand piskvorkyConfigCommand(String key, String value) {
		return new PiskvorkyConfigCommand(key, value);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public PlayCommand playCommand(List<Move> moves) {
		return new PlayCommand(moves);
	}

	@Bean
    public ProtocolVersionCommand protocolVersionCommand() {
        return ProtocolVersionCommand.INSTANCE;
    }

	@Bean
	public QuitCommand quitCommand(ConfigurableApplicationContext applicationContext) {
		return new QuitCommand(applicationContext);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RegGenMoveCommand regGenMoveCommand(Side side) {
		return new RegGenMoveCommand(side);
	}

    @Bean
    public VersionCommand versionCommand(@Value("${spring.application.version:unknown}") String version) {
        return new VersionCommand(version);
    }

}
