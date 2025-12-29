package cz.doleckovi.piskvorky.gtp.autoconfigure;

import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.parser.GTPLexer;
import cz.doleckovi.piskvorky.gtp.parser.GTPParser;
import cz.doleckovi.piskvorky.gtp.parser.GTPParserListener;
import cz.doleckovi.piskvorky.gtp.parser.ParserListener;
import org.antlr.v4.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class ParserConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(ParserConfiguration.class);

	@Bean(autowireCandidate = false)
	public ParserListener parserListener(CommandFactory commandFactory, CommandConsumer commandConsumer) {
		return new ParserListener(commandFactory, commandConsumer);
	}

	@Bean
	@ConditionalOnMissingBean(CharStream.class)
	public CharStream charStream(GTPProperties properties) throws IOException {
		if (properties.getScanner() != null) {
			var input = properties.getScanner();
			// TODO Add support for charset
			if (input.isFile()) {
				LOG.info("Reading from file {}", input.getFile());
				return CharStreams.fromPath(input.getFile().toPath());
			} else {
				LOG.info("Reading from stream {}", input);
				return CharStreams.fromStream(input.getInputStream());
			}
		}
		var console = System.console();
		if (console != null) {
			LOG.info("Reading from console");
			var result = new UnbufferedCharStream(console.reader());
			result.name = console.isTerminal() ? "terminal" : "console";
			return result;
		}
		LOG.info("Reading from STDIN");
		var result = new UnbufferedCharStream(System.in, 256, Charset.forName(System.getProperty("stdin.encoding")));
		result.name = "STDIN";
		return result;
	}

	@Bean
	public GTPLexer gtpLexer(CharStream charStream) {
		var result = new GTPLexer(charStream);
		if (charStream instanceof UnbufferedCharStream)
			result.setTokenFactory(new CommonTokenFactory(true));
		return result;
	}

	@Bean
	public GTPParser gtpParser(GTPLexer lexer, List<GTPParserListener> listeners,
			List<ANTLRErrorListener> errorListeners)
	{
		TokenStream tokenStream;
		if (lexer.getInputStream() instanceof UnbufferedCharStream) {
			tokenStream = new UnbufferedTokenStream<CommonToken>(lexer);
		} else {
			tokenStream = new CommonTokenStream(lexer);
		}
		var result = new GTPParser(tokenStream);
		result.setBuildParseTree(false);
		listeners.forEach(result::addParseListener);
		result.removeErrorListeners();
		errorListeners.forEach(result::addErrorListener);
		return result;
	}

}
