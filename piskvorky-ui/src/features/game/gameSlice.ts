import {createSlice, PayloadAction} from "@reduxjs/toolkit"
import {AppThunk, RootState} from "../../app/store"
import {Game, Move, Player} from "./gameAPI";

const initialState: Game = {
    board: {
        width: 15,
        height: 15,
    },
    moves: [],
    status: "idle"
}

export const gameSlice = createSlice({
    name: "game",
    initialState: initialState,
    reducers: {
        makeMove: (state, action: PayloadAction<Move>) => {
            console.log("Move made: " + action)
            state.moves.push(action.payload);
        }
    }
})

export const { makeMove } = gameSlice.actions

export const selectGame = (state: RootState) => state.game

export default gameSlice.reducer

export const tryMove =
    (column: number, row: number): AppThunk =>
        (dispatch, getState) => {
            const game = selectGame(getState())
            const moves = game.moves
            if (moves.some(move => move.column == column && move.row == row)) return
            const player = moves[moves.length - 1]?.player == Player.WHITE ? Player.BLACK : Player.WHITE
            dispatch(makeMove({column: column, row: row, player: player}))
        }
