package com.tictactoe

import org.springframework.stereotype.Service
import java.util.*

@Service
class Game {
    val node = load()
    val scanner = Scanner(System.`in`)

    fun move(state: State, ans: Int): Status {
        if(state.node.str[ans]!='_') {
            return Status("Wrong Move", null)
        }
        //state.node.print()
//        do {
//            print("Your answer: ")
//            val ans = scanner.nextLine()
        state.move(ans, 'X')
        if (state.node.winner != null && state.node.winner != '_') {
//                println("Winner is ${state.node.winner!!.toUpperCase()}")
            return Status(state.node.str, state.node.winner!!.toUpperCase())
        }
        if (state.node.child.isEmpty()) {
//                state.node.print()
            return Status(state.node.str, Value.None.chr)
        }
        state.automove()
        if (state.node.winner != null && state.node.winner != '_') {
//                println("Winner is ${state.node.winner!!.toUpperCase()}")
            return Status(state.node.str, state.node.winner!!.toUpperCase())
        }
//            state.node.print()

//        } while (state.node.child.isNotEmpty())
        if (state.node.child.isEmpty()) {
            return Status(state.node.str, Value.None.chr)
        }
        return Status(state.node.str, null)
    }


    fun play() {
        val state = State(node, Difficulty.Difficult)
        state.node.print()
        do {
            print("Your answer: ")
            val ans = scanner.nextLine()
            state.move(ans.toInt(), 'X')
            if (state.node.winner != null && state.node.winner != '_') {
                println("Winner is ${state.node.winner!!.toUpperCase()}")
                return
            }
            if (state.node.child.isEmpty()) {
                state.node.print()
                println("Match Drawn")
                return
            }
            state.automove()
            if (state.node.winner != null && state.node.winner != '_') {
                println("Winner is ${state.node.winner!!.toUpperCase()}")
                return
            }
            state.node.print()

        } while (state.node.child.isNotEmpty())
        if (state.node.child.isEmpty()) {
            println("Match Drawn")
            return
        }
    }

}

class Status(val string: String, val winner: Char?)

enum class Difficulty {
    Easy,
    Medium,
    Difficult
}

class State(var node: Node, private val difficulty: Difficulty) {
    fun move(index: Int, player: Char): Unit =
        if (index < 0 || index > 8) {

        } else if (node.str[index] != '_') {

        } else {
            val str = java.lang.StringBuilder(node.str)
            str[index] = player.toUpperCase()
            val s = str.toString()
            node = node.child.filter {
                it.str == s
            }.first()!!
        }

    fun automove(): Unit {
        node = when(difficulty) {
            Difficulty.Easy -> node.autoRandomMove()
            Difficulty.Medium -> node.autoGoodMove()
            Difficulty.Difficult -> node.autoBestMove()
        }
    }
}