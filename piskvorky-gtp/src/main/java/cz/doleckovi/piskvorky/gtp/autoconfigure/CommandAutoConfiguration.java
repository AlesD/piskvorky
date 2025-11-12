package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.GTPCommand;
import cz.doleckovi.piskvorky.gtp.command.*;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.Arrays;
import java.util.List;

@AutoConfiguration
public class CommandAutoConfiguration {

    @Value("${spring.application.name:piskvorky}")
    String name;
    @Value("${spring.application.version:unknown}")
    String version;
    @Autowired
    ListableBeanFactory beanFactory;

    private String[] commandNames;

    private String[] sortedCommandNames() {
        if (this.commandNames == null) {
            var commandNames = beanFactory.getBeanNamesForType(GTPCommand.class, true, true);
            Arrays.sort(commandNames);
            this.commandNames = commandNames;
        }
        return commandNames;
    }

    @Bean(name = ProtocolVersionCommand.NAME)
    public ProtocolVersionCommand protocolVersionCommand() {
        return new ProtocolVersionCommand();
    }

    @Bean(name = NameCommand.NAME)
    public NameCommand nameCommand() {
        return new NameCommand(name);
    }

    @Bean(name = VersionCommand.NAME)
    public VersionCommand versionCommand() {
        return new VersionCommand(version);
    }

    @Bean(name = KnownCommandCommand.NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KnownCommandCommand knownCommandCommand(String commandName) {
        return new KnownCommandCommand(Arrays.binarySearch(sortedCommandNames(), commandName) >= 0);
    }

    @Bean(name = ListCommandsCommand.NAME)
    public ListCommandsCommand listCommandsCommand() {
        return new ListCommandsCommand(List.of(sortedCommandNames()));
    }

    @Bean(name = EchoCommand.NAME)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EchoCommand echoCommand(String text) {
        return new EchoCommand(text);
    }

    @Bean
    public CommandFactoryImpl commandFactory() {
        return new CommandFactoryImpl(beanFactory);
    }

}
