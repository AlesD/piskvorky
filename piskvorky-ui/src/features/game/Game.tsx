import {useAppDispatch, useAppSelector} from "../../app/hooks"
import {selectGame, tryMove} from "./gameSlice";
import styles from "./Game.module.css"
import {Player} from "./gameAPI";
import React from "react";

interface FieldParameters { row: number, column: number, stone: Player|undefined}
interface RowParameters { row: number, stones: Player[]}

function Field({row, column, stone} : FieldParameters) {
  const dispatch = useAppDispatch()
  switch (stone) {
    case Player.WHITE: return <td className={styles.field}><img src="/white.svg" alt="white" /></td>
    case Player.BLACK: return <td className={styles.field}><img src="/black.svg" alt="black"/></td>
  }
  return <td className={styles.field} onClick={() => dispatch(tryMove(column, row))}/>
}

function Row({row, stones}: RowParameters) {
  const fields = []
  for (let column = 0; column < stones.length; ++column) {
    const stone = stones[column]
    fields.push(<Field key={"r" + row + "c" + column} row={row} column={column} stone={stone}/>)
  }
  return <tr>{fields}</tr>
}

export function Game() {
  const game = useAppSelector(selectGame)
  const board = game.board
  const stones = Array<Player[]>(board.height);
  game.moves.forEach(move => {
    const row = stones[move.row] ?? (stones[move.row] = Array<Player>(board.width))
    row[move.column] = move.player
  })
  const rows = []
  for (let row = 0; row < board.height; ++row) {
    const stones_ = stones[row] ?? Array<Player>(board.width)
    rows.push(<Row key={"row" + row} row={row} stones={stones_ ?? Array<Player>(board.width)}/>)
  }
  return <table className={styles.game}><tbody>{rows}</tbody></table>
}
