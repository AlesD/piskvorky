// Generated from C:/Users/ales_/IdeaProjects/piskvorky/piskvorky-gtp/src/main/antlr4/cz/doleckovi/piskvorky/gtp/command/parser/GTPParser.g4 by ANTLR 4.13.2

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class GTPParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INTEGER=1, COMMAND=2, INTERRUPT=3, IGNORED=4, VERTEX=5, WHITE=6, BLACK=7, 
		BLOCK=8, ARG_IGNORED=9, KEY=10, KVP_IGNORED=11, TEXT_IGNORED=12, TEXT=13;
	public static final int
		RULE_action = 0, RULE_command = 1, RULE_moves = 2, RULE_move = 3, RULE_color = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"action", "command", "moves", "move", "color"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'# interrupt'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INTEGER", "COMMAND", "INTERRUPT", "IGNORED", "VERTEX", "WHITE", 
			"BLACK", "BLOCK", "ARG_IGNORED", "KEY", "KVP_IGNORED", "TEXT_IGNORED", 
			"TEXT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "GTPParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	CommandFactory factory;

	public GTPParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ActionContext extends ParserRuleContext {
		public Command value;
		public CommandContext command() {
			return getRuleContext(CommandContext.class,0);
		}
		public TerminalNode INTEGER() { return getToken(GTPParser.INTEGER, 0); }
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_action);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INTEGER) {
				{
				setState(10);
				match(INTEGER);
				}
			}

			setState(13);
			command();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CommandContext extends ParserRuleContext {
		public Command value;
		public CommandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_command; }
	 
		public CommandContext() { }
		public void copyFrom(CommandContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TextCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public TerminalNode TEXT() { return getToken(GTPParser.TEXT, 0); }
		public TextCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterTextCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitTextCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitTextCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VertexCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public TerminalNode VERTEX() { return getToken(GTPParser.VERTEX, 0); }
		public VertexCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterVertexCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitVertexCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitVertexCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SimpleCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public SimpleCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterSimpleCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitSimpleCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitSimpleCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class InterruptCommandContext extends CommandContext {
		public TerminalNode INTERRUPT() { return getToken(GTPParser.INTERRUPT, 0); }
		public InterruptCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterInterruptCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitInterruptCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitInterruptCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MoveCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public MoveContext move() {
			return getRuleContext(MoveContext.class,0);
		}
		public MoveCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterMoveCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitMoveCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitMoveCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class KVPCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public TerminalNode KEY() { return getToken(GTPParser.KEY, 0); }
		public TerminalNode TEXT() { return getToken(GTPParser.TEXT, 0); }
		public KVPCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterKVPCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitKVPCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitKVPCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ColorCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public ColorContext color() {
			return getRuleContext(ColorContext.class,0);
		}
		public ColorCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterColorCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitColorCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitColorCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NumberCommandContext extends CommandContext {
		public Token number;
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public TerminalNode INTEGER() { return getToken(GTPParser.INTEGER, 0); }
		public NumberCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterNumberCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitNumberCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitNumberCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EmptyCommandContext extends CommandContext {
		public TerminalNode EOF() { return getToken(GTPParser.EOF, 0); }
		public EmptyCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterEmptyCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitEmptyCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitEmptyCommand(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class MovesCommandContext extends CommandContext {
		public TerminalNode COMMAND() { return getToken(GTPParser.COMMAND, 0); }
		public MovesContext moves() {
			return getRuleContext(MovesContext.class,0);
		}
		public MovesCommandContext(CommandContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterMovesCommand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitMovesCommand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitMovesCommand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CommandContext command() throws RecognitionException {
		CommandContext _localctx = new CommandContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_command);
		try {
			setState(33);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				_localctx = new TextCommandContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(15);
				match(COMMAND);
				setState(16);
				match(TEXT);
				}
				break;
			case 2:
				_localctx = new KVPCommandContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(17);
				match(COMMAND);
				setState(18);
				match(KEY);
				setState(19);
				match(TEXT);
				}
				break;
			case 3:
				_localctx = new MovesCommandContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(20);
				match(COMMAND);
				setState(21);
				moves();
				}
				break;
			case 4:
				_localctx = new MoveCommandContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(22);
				match(COMMAND);
				setState(23);
				move();
				}
				break;
			case 5:
				_localctx = new VertexCommandContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(24);
				match(COMMAND);
				setState(25);
				match(VERTEX);
				}
				break;
			case 6:
				_localctx = new ColorCommandContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(26);
				match(COMMAND);
				setState(27);
				color();
				}
				break;
			case 7:
				_localctx = new NumberCommandContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(28);
				match(COMMAND);
				setState(29);
				((NumberCommandContext)_localctx).number = match(INTEGER);
				}
				break;
			case 8:
				_localctx = new SimpleCommandContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(30);
				match(COMMAND);
				}
				break;
			case 9:
				_localctx = new InterruptCommandContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(31);
				match(INTERRUPT);
				}
				break;
			case 10:
				_localctx = new EmptyCommandContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(32);
				match(EOF);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MovesContext extends ParserRuleContext {
		public List<Move> value;
		public List<MoveContext> move() {
			return getRuleContexts(MoveContext.class);
		}
		public MoveContext move(int i) {
			return getRuleContext(MoveContext.class,i);
		}
		public MovesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moves; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterMoves(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitMoves(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitMoves(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MovesContext moves() throws RecognitionException {
		MovesContext _localctx = new MovesContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_moves);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(35);
				move();
				}
				}
				setState(38); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 448L) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MoveContext extends ParserRuleContext {
		public Move value;
		public MoveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_move; }
	 
		public MoveContext() { }
		public void copyFrom(MoveContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlockMoveContext extends MoveContext {
		public TerminalNode BLOCK() { return getToken(GTPParser.BLOCK, 0); }
		public TerminalNode VERTEX() { return getToken(GTPParser.VERTEX, 0); }
		public BlockMoveContext(MoveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterBlockMove(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitBlockMove(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitBlockMove(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class NormalMoveContext extends MoveContext {
		public ColorContext color() {
			return getRuleContext(ColorContext.class,0);
		}
		public TerminalNode VERTEX() { return getToken(GTPParser.VERTEX, 0); }
		public NormalMoveContext(MoveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterNormalMove(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitNormalMove(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitNormalMove(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MoveContext move() throws RecognitionException {
		MoveContext _localctx = new MoveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_move);
		try {
			setState(45);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WHITE:
			case BLACK:
				_localctx = new NormalMoveContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(40);
				color();
				setState(41);
				match(VERTEX);
				}
				break;
			case BLOCK:
				_localctx = new BlockMoveContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(43);
				match(BLOCK);
				setState(44);
				match(VERTEX);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ColorContext extends ParserRuleContext {
		public Side value;
		public ColorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_color; }
	 
		public ColorContext() { }
		public void copyFrom(ColorContext ctx) {
			super.copyFrom(ctx);
			this.value = ctx.value;
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class WhiteColorContext extends ColorContext {
		public TerminalNode WHITE() { return getToken(GTPParser.WHITE, 0); }
		public WhiteColorContext(ColorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterWhiteColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitWhiteColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitWhiteColor(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BlackColorContext extends ColorContext {
		public TerminalNode BLACK() { return getToken(GTPParser.BLACK, 0); }
		public BlackColorContext(ColorContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).enterBlackColor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GTPParserListener ) ((GTPParserListener)listener).exitBlackColor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GTPParserVisitor ) return ((GTPParserVisitor<? extends T>)visitor).visitBlackColor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColorContext color() throws RecognitionException {
		ColorContext _localctx = new ColorContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_color);
		try {
			setState(49);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case WHITE:
				_localctx = new WhiteColorContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(47);
				match(WHITE);
				}
				break;
			case BLACK:
				_localctx = new BlackColorContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				match(BLACK);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\r4\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0001"+
		"\u0000\u0003\u0000\f\b\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001\"\b"+
		"\u0001\u0001\u0002\u0004\u0002%\b\u0002\u000b\u0002\f\u0002&\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0003\u0003.\b\u0003"+
		"\u0001\u0004\u0001\u0004\u0003\u00042\b\u0004\u0001\u0004\u0000\u0000"+
		"\u0005\u0000\u0002\u0004\u0006\b\u0000\u0000;\u0000\u000b\u0001\u0000"+
		"\u0000\u0000\u0002!\u0001\u0000\u0000\u0000\u0004$\u0001\u0000\u0000\u0000"+
		"\u0006-\u0001\u0000\u0000\u0000\b1\u0001\u0000\u0000\u0000\n\f\u0005\u0001"+
		"\u0000\u0000\u000b\n\u0001\u0000\u0000\u0000\u000b\f\u0001\u0000\u0000"+
		"\u0000\f\r\u0001\u0000\u0000\u0000\r\u000e\u0003\u0002\u0001\u0000\u000e"+
		"\u0001\u0001\u0000\u0000\u0000\u000f\u0010\u0005\u0002\u0000\u0000\u0010"+
		"\"\u0005\r\u0000\u0000\u0011\u0012\u0005\u0002\u0000\u0000\u0012\u0013"+
		"\u0005\n\u0000\u0000\u0013\"\u0005\r\u0000\u0000\u0014\u0015\u0005\u0002"+
		"\u0000\u0000\u0015\"\u0003\u0004\u0002\u0000\u0016\u0017\u0005\u0002\u0000"+
		"\u0000\u0017\"\u0003\u0006\u0003\u0000\u0018\u0019\u0005\u0002\u0000\u0000"+
		"\u0019\"\u0005\u0005\u0000\u0000\u001a\u001b\u0005\u0002\u0000\u0000\u001b"+
		"\"\u0003\b\u0004\u0000\u001c\u001d\u0005\u0002\u0000\u0000\u001d\"\u0005"+
		"\u0001\u0000\u0000\u001e\"\u0005\u0002\u0000\u0000\u001f\"\u0005\u0003"+
		"\u0000\u0000 \"\u0005\u0000\u0000\u0001!\u000f\u0001\u0000\u0000\u0000"+
		"!\u0011\u0001\u0000\u0000\u0000!\u0014\u0001\u0000\u0000\u0000!\u0016"+
		"\u0001\u0000\u0000\u0000!\u0018\u0001\u0000\u0000\u0000!\u001a\u0001\u0000"+
		"\u0000\u0000!\u001c\u0001\u0000\u0000\u0000!\u001e\u0001\u0000\u0000\u0000"+
		"!\u001f\u0001\u0000\u0000\u0000! \u0001\u0000\u0000\u0000\"\u0003\u0001"+
		"\u0000\u0000\u0000#%\u0003\u0006\u0003\u0000$#\u0001\u0000\u0000\u0000"+
		"%&\u0001\u0000\u0000\u0000&$\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000"+
		"\u0000\'\u0005\u0001\u0000\u0000\u0000()\u0003\b\u0004\u0000)*\u0005\u0005"+
		"\u0000\u0000*.\u0001\u0000\u0000\u0000+,\u0005\b\u0000\u0000,.\u0005\u0005"+
		"\u0000\u0000-(\u0001\u0000\u0000\u0000-+\u0001\u0000\u0000\u0000.\u0007"+
		"\u0001\u0000\u0000\u0000/2\u0005\u0006\u0000\u000002\u0005\u0007\u0000"+
		"\u00001/\u0001\u0000\u0000\u000010\u0001\u0000\u0000\u00002\t\u0001\u0000"+
		"\u0000\u0000\u0005\u000b!&-1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}