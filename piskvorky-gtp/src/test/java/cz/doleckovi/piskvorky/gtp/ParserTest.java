package cz.doleckovi.piskvorky.gtp;

import org.antlr.v4.runtime.*;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;

class ParserTest implements WithAssertions {

    @ParameterizedTest
    @ValueSource(strings = {
        "protocol_version",
        "known_command known_command",
        "board_size 10",
        "play white a1",
        "genmove black",
        "color H12",
        "echo B12 can't be played w/o defence of 5th row black will put stone at offset 5",
        "gogui-action_forward",
        "gogui-action_backward 3",
        "gogui-play_sequence white a3 b c7 W a2 Black d1",
        "# interrupt",
        "piskvorky-config depth 3",
        "piskvorky-config white_name John F. Smith"
    })
    void testHelp(String input) {
        var parser = new GTPParser(null);
        parser.setBuildParseTree(false);
        var lexer = new GTPLexer(CharStreams.fromString(input));
        lexer.setLine(1);
        lexer.setCharPositionInLine(0);
        parser.setInputStream(new CommonTokenStream(lexer));
        var result = parser.action();
        if (result.exception != null)
            throw result.exception;
    }

}
