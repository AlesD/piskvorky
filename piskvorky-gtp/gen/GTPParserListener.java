// Generated from C:/Users/ales_/IdeaProjects/piskvorky/piskvorky-gtp/src/main/antlr4/cz/doleckovi/piskvorky/gtp/command/parser/GTPParser.g4 by ANTLR 4.13.2

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GTPParser}.
 */
public interface GTPParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GTPParser#action}.
	 * @param ctx the parse tree
	 */
	void enterAction(GTPParser.ActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GTPParser#action}.
	 * @param ctx the parse tree
	 */
	void exitAction(GTPParser.ActionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TextCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterTextCommand(GTPParser.TextCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TextCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitTextCommand(GTPParser.TextCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code KVPCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterKVPCommand(GTPParser.KVPCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code KVPCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitKVPCommand(GTPParser.KVPCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MovesCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterMovesCommand(GTPParser.MovesCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MovesCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitMovesCommand(GTPParser.MovesCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MoveCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterMoveCommand(GTPParser.MoveCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MoveCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitMoveCommand(GTPParser.MoveCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VertexCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterVertexCommand(GTPParser.VertexCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VertexCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitVertexCommand(GTPParser.VertexCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ColorCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterColorCommand(GTPParser.ColorCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ColorCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitColorCommand(GTPParser.ColorCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterNumberCommand(GTPParser.NumberCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitNumberCommand(GTPParser.NumberCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SimpleCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterSimpleCommand(GTPParser.SimpleCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SimpleCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitSimpleCommand(GTPParser.SimpleCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code InterruptCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterInterruptCommand(GTPParser.InterruptCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code InterruptCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitInterruptCommand(GTPParser.InterruptCommandContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EmptyCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void enterEmptyCommand(GTPParser.EmptyCommandContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EmptyCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 */
	void exitEmptyCommand(GTPParser.EmptyCommandContext ctx);
	/**
	 * Enter a parse tree produced by {@link GTPParser#moves}.
	 * @param ctx the parse tree
	 */
	void enterMoves(GTPParser.MovesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GTPParser#moves}.
	 * @param ctx the parse tree
	 */
	void exitMoves(GTPParser.MovesContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NormalMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 */
	void enterNormalMove(GTPParser.NormalMoveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NormalMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 */
	void exitNormalMove(GTPParser.NormalMoveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 */
	void enterBlockMove(GTPParser.BlockMoveContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 */
	void exitBlockMove(GTPParser.BlockMoveContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhiteColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 */
	void enterWhiteColor(GTPParser.WhiteColorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhiteColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 */
	void exitWhiteColor(GTPParser.WhiteColorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlackColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 */
	void enterBlackColor(GTPParser.BlackColorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlackColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 */
	void exitBlackColor(GTPParser.BlackColorContext ctx);
}