package cz.doleckovi.piskvorky.app;

import cz.doleckovi.piskvorky.core.GameImpl;
import cz.doleckovi.piskvorky.core.board.BoardImpl;
import cz.doleckovi.piskvorky.gtp.action.ActionFactory;
import cz.doleckovi.piskvorky.gtp.command.CompositeCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.CommandFactory;
import cz.doleckovi.piskvorky.gtp.command.GtpCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.GoGuiCommandFactory;
import cz.doleckovi.piskvorky.gtp.command.gogui.rules.RulesCommandFactory;
import cz.doleckovi.piskvorky.gtp.parser.AntlrCommandParser;
import cz.doleckovi.piskvorky.gtp.parser.CommandParser;
import cz.doleckovi.piskvorky.gtp.session.Session;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
import cz.doleckovi.piskvorky.gtp.transport.StdioTransport;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ThreadFactory;

/** Configuration for MVP single session over STDIO.
 * <p>The session uses STDIO and closes aaplication context when it ends.</p>
 */
@Configuration(proxyBeanMethods = false)
@Profile("gtp")
public class GtpConfiguration {

	@Bean
	public GtpTransport transport() {
		return StdioTransport.stdio();
	}

	@Bean
	public CommandFactory commandFactory() {
		return new CompositeCommandFactory(new GtpCommandFactory(), new GoGuiCommandFactory(), new RulesCommandFactory());
	}

	@Bean
	public ActionFactory actionFactory() {
		return new ActionFactory() { };
	}

	@Bean
	public CommandParser commandParser(CommandFactory commandFactory, ActionFactory actionFactory) {
		return new AntlrCommandParser(commandFactory, actionFactory);
	}

	@Bean
	public ThreadFactory sessionThreads() {
		return Thread.ofPlatform().factory();
	}

	@Bean(destroyMethod = "close")
	public Session session(ThreadFactory sessionThreads, GtpTransport transport, CommandParser parser,
			ConfigurableApplicationContext context) {
		return new Session("stdio", sessionThreads, transport, parser, BoardImpl::new, GameImpl::new,
				session -> context.close());
	}

}
