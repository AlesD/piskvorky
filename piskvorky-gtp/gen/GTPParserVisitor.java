// Generated from C:/Users/ales_/IdeaProjects/piskvorky/piskvorky-gtp/src/main/antlr4/cz/doleckovi/piskvorky/gtp/command/parser/GTPParser.g4 by ANTLR 4.13.2

import cz.doleckovi.piskvorky.api.board.Side;
import cz.doleckovi.piskvorky.api.board.Stone;
import cz.doleckovi.piskvorky.gtp.Command;
import cz.doleckovi.piskvorky.gtp.CommandFactory;
import cz.doleckovi.piskvorky.gtp.Move;
import cz.doleckovi.piskvorky.gtp.Vertex;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GTPParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GTPParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GTPParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(GTPParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TextCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextCommand(GTPParser.TextCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code KVPCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKVPCommand(GTPParser.KVPCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MovesCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMovesCommand(GTPParser.MovesCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MoveCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMoveCommand(GTPParser.MoveCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VertexCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVertexCommand(GTPParser.VertexCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ColorCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColorCommand(GTPParser.ColorCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumberCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberCommand(GTPParser.NumberCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SimpleCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleCommand(GTPParser.SimpleCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code InterruptCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInterruptCommand(GTPParser.InterruptCommandContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EmptyCommand}
	 * labeled alternative in {@link GTPParser#command}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmptyCommand(GTPParser.EmptyCommandContext ctx);
	/**
	 * Visit a parse tree produced by {@link GTPParser#moves}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMoves(GTPParser.MovesContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NormalMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNormalMove(GTPParser.NormalMoveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlockMove}
	 * labeled alternative in {@link GTPParser#move}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockMove(GTPParser.BlockMoveContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhiteColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhiteColor(GTPParser.WhiteColorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlackColor}
	 * labeled alternative in {@link GTPParser#color}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlackColor(GTPParser.BlackColorContext ctx);
}