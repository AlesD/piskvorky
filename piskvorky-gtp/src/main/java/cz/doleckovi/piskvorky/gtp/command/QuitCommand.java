package cz.doleckovi.piskvorky.gtp.command;

import org.springframework.context.ConfigurableApplicationContext;

@CommandInfo(name = "quit", description = "Ends the application")
public class QuitCommand implements Command {

	private ConfigurableApplicationContext applicationContext;

	public QuitCommand(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public String call() throws CommandException {
		applicationContext.close();
		return null;
	}

	@Override
	public String toString() {
		return QuitCommand.class.getSimpleName() + "[]";
	}

}
