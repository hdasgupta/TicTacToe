package com.tictactoe

import org.springframework.stereotype.Service
import java.beans.beancontext.BeanContextChild
import java.util.*
import java.util.stream.Collectors

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
        state.automove('O')
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
        val state = State(node)
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
            state.automove('O')
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

class State(var node: Node) {
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

    fun automove(player: Char): Unit {
        val minLoosing = node.child.maxOf { it.oLoseWithSteps }
        var answer = node.child.filter { it.oLoseWithSteps == minLoosing }
        val maxWining = answer.minOf { it.xWinningPossibilities.size - it.oWiningPossibilities.size }
        var answers = answer.filter { it.xWinningPossibilities.size - it.oWiningPossibilities.size == maxWining }
        node = if(answers.isEmpty()) {
            val minLoose = node.child.minOf { it.drawPossibilities.size }
            val answers = node.child.filter { it.drawPossibilities.size == minLoose }
            answers.random()
        } else {
            answers.random()
        }
    }

    fun filter(it: NodeScore) : NodeScore {
        return if(it.child.isEmpty()) {
            it
        }else {
            val filtered = it.child.map { i->filter(i) }
            val fastestLoss = filtered.minOf { i->i.losing }
            val notLosing = filtered.filter { i->
                i.losing > fastestLoss &&
                        ((it.wining< it.losing) ||
                                (it.wining> it.losing && it.drawn>0)) }

            NodeScore(
                it.node,
                it.player,
                notLosing,
                it.wining,
                it.losing,
                it.drawn
            )
        }
    }

    fun score(n: Node, player: Char): NodeScore {
        val children = n.child.toSet()
        return NodeScore(
            n,
            player,
            n.child.map { score(it, if(player=='X') 'O' else 'X') },
            winningPaths(children, player).level,
            winningPaths(children, if(player=='X') 'O' else 'X').level,
            drawnPaths(children).level
        )
    }

    fun winningPaths(n: Set<Node>, player: Char, level: Int = 0): WinningOutput {
        if (n.isEmpty()) {
            return WinningOutput(setOf(), Int.MAX_VALUE)
        }
        val success = n.filter { it.winner == player.toUpperCase() }
        return if (success.isEmpty()) {
            winningPaths(n.map { it.child }.flatten().toSet(), player, level + 1)
        } else {
            WinningOutput(getParents(success, level), level-1)
        }
    }

    fun drawnPaths(n: Set<Node>, level: Int = 0): WinningOutput {
        return if (n.all { it.child.isEmpty() }) {
            WinningOutput(getParents(n.toList(), level), level)
        } else {
    //        val success = node.filter { it.winner == player.toUpperCase() }
    //        if (success.isEmpty()) {
            drawnPaths(n.map { it.child }.flatten().toSet(), level + 1)
    //        } else {
    //            return WinningOutput(getParents(success, level), level)
    //        }
        }
    }


    fun getParents(node: List<Node>, level: Int): Set<Node> {
        if (level == 0) {
            return node.toSet()
        } else {
            return getParents(node.map { it.parent }.flatten(), level - 1)
        }
    }

    class NodeScore(val node:Node, val player: Char,  val child: List<NodeScore> , val wining: Int, val losing: Int, val drawn: Int) {
        override fun toString(): String =
            "${node.str} (${wining}, ${losing}, ${drawn})"
    }

    class WinningOutput(val nodes:Set<Node>, val level: Int)
}