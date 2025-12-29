package cz.doleckovi.piskvorky.gtp.parser;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AntlrCommandParserIT implements WithAssertions {

//	@Configuration
//	@Import(GTPAutoConfiguration.GTPConfiguration.class)
//	public static class TestConfiguration {
//
//		@Bean
//		public LineSupplier lineSupplier() {
//			var commands = """
//			protocol_version
//			list_commands
//			boardsize 10
//			""".lines().iterator();
//			return () -> {
//				return commands.hasNext() ? commands.next() : null;
//			};
//		}
//
//	}

	@Autowired
	AntlrCommandParser antlrCommandParser;

	@ParameterizedTest
	@ValueSource(strings = {
			"protocol_version",
			"boardsize 10",
			"known_command gogui-setup",
			"gogui-setup white a1 b b2 W c3 Black d4",
			"play wHIte e5",
			"reg_genmove black",
			"color a1",
			"echo B12 can't be played w/o defence of 5th row black will put stone at offset 5",
			"# interrupt",
			"piskvorky-config depth 3",
			"piskvorky-config white_name John F. Smith"
	})
	void parseLine(String line) {
		assertThatCode(() -> antlrCommandParser.parse(line))
				.doesNotThrowAnyException();
	}

}