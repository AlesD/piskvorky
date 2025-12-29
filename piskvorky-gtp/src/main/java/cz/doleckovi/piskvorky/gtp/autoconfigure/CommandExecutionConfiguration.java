package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.command.execution.CommandExecutorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ExecutorCompletionService;

@Configuration
public class CommandExecutionConfiguration {

	@Bean(autowireCandidate = false)
	@Lazy
	public CommandExecutorService commandExecutorService() {
		return new CommandExecutorService();
	}

	@Bean(autowireCandidate = false)
	@Lazy
	public ExecutorCompletionService<String> executorCompletionService() {
		return new ExecutorCompletionService<>(commandExecutorService());
	}

	@Bean
	@ConditionalOnMissingBean(CommandConsumer.class)
	public CommandConsumer commandConsumer() {
		return executorCompletionService()::submit;
	}

}
