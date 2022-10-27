package chess

import kotlin.math.abs

var lastTurn = ""
const val errorMessage = "Invalid Input"
val revert: Char.() -> Char = {
    when (this) {
        'W' -> 'B'
        'B' -> 'W'
        else -> ' '
    }
}

fun printPlusesRank() = println("  +" + "---+".repeat(8))
fun printBottomRank() = println("    a   b   c   d   e   f   g   h")
fun printBoard(board: MutableList<MutableList<Char>>) {
    for (i in 0..7) {
        printPlusesRank()
        print("${8 - i} |")
        for (j in 0..7) {
            print(" ${board[i][j]} |")
        }
        print("\n")
    }
    printPlusesRank()
    printBottomRank()
}

fun updateBoard(
    move: String,
    color: Char,
    board: MutableList<MutableList<Char>>,
    capture: Boolean
): Boolean {
    board[8 - move[1].code % 48][move[0].code % 97] = ' '
    board[8 - move[3].code % 48][move[2].code % 97] = color
    if (capture) board[8 - move[1].code % 48][move[2].code % 97] = ' '
    printBoard(board)
    lastTurn = move
    return if (!checkWinningCondition(board)) checkNoStalemateCondition(color, board) else false
}

fun checkWinningCondition(board: MutableList<MutableList<Char>>): Boolean {
    if (board[0].contains('W')) {
        println("White Wins!")
        println("Bye!")
        return true
    } else {
        if (board[7].contains('B')) {
            println("Black Wins!")
            println("Bye!")
            return true
        }
        return false
    }
}

fun haveValidMove(fromIndex: Int, toIndex: Int, color: Char, board: MutableList<MutableList<Char>>): Boolean {
    val gap = when (color) {
        'W' -> -1
        else -> 1
    }
    for (i in fromIndex..toIndex) {
        for (j in 0..7) {
            if (board[i + gap][j] != color.revert()) return true
        }
    }
    return false
}

fun checkNoStalemateCondition(color: Char, board: MutableList<MutableList<Char>>): Boolean {
    when (color) {
        'B' -> {
            if (haveValidMove(2, 6, 'W', board)) return true
        }
        else -> {
            if (haveValidMove(1, 5, 'B', board)) return true
        }
    }
    println("Stalemate!")
    println("Bye!")
    return false
}

fun makeMove(
    name: String,
    color: Char,
    board: MutableList<MutableList<Char>>
): Boolean {
    var move: String
    val diffOne: Int
    val diffTwo: Int
    when (color) {
        'W' -> {
            diffOne = 1
            diffTwo = 2
        }
        else -> {
            diffOne = -1
            diffTwo = -2
        }
    }
    while (true) {
        println("$name's turn:")
        move = readLine()!!
        if (move == "exit") {
            println("Bye!")
            return false
        }
        if (move.matches(Regex("[a-h][1-8][a-h][1-8]")) && (color == 'B' || color == 'W')) {
            if (getPawn(move.substring(0, 2), board) == color) {
                if (move[0] == move[2]) {
                    if (getPawn(
                            move.substring(2, 4),
                            board
                        ) == ' ' && move[3] - move[1] == diffOne || move[1].code % 48 % 5 == 2 && move[3] - move[1] == diffTwo
                    ) return updateBoard(move, color, board, false)
                    println(errorMessage)
                    continue
                } else {
                    if (abs(move[0] - move[2]) == 1 && move[3] - move[1] == diffOne) {
                        when (getPawn(move.substring(2, 4), board)) {
                            ' ' -> {
                                if (getPawn(
                                        move[2] + (move[3] - 1).toString(),
                                        board
                                    ) == color.revert() && lastTurn[3] - lastTurn[1] == -diffTwo
                                ) return updateBoard(move, color, board, true)
                            }
                            color.revert() -> return updateBoard(move, color, board, false)
                        }
                    }
                    println(errorMessage)
                    continue
                }
            } else {
                println(
                    if (color == 'B') "No black pawn at ${move.substring(0, 2)}"
                    else "No white pawn at ${move.substring(0, 2)}"
                )
                continue
            }
        } else {
            println(errorMessage)
            continue
        }
    }
}

fun getPawn(position: String, board: MutableList<MutableList<Char>>): Char =
    board[8 - position[1].code % 48][position[0].code % 97]

fun main() {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val player1Name = readLine()!!
    println("Second Player's name:")
    val player2Name = readLine()!!
    val board = mutableListOf(
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('B', 'B', 'B', 'B', 'B', 'B', 'B', 'B'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
        mutableListOf('W', 'W', 'W', 'W', 'W', 'W', 'W', 'W'),
        mutableListOf(' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '),
    )
    printBoard(board)
    while (true) {
        if (!makeMove(player1Name, 'W', board)) break
        if (!makeMove(player2Name, 'B', board)) break
    }
}
