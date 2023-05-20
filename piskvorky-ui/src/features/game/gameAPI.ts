export enum Player {
    WHITE,
    BLACK
}

export interface Board {
    width: number,
    height: number
}

export interface Move {
    column: number,
    row: number,
    player: Player
}

export interface Game {
    board: Board,
    moves: Move[],
    status: "idle" | "loading" | "failed"
}