package com.tictactoe

import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.*

@Component
class Game {
    private val node = load()
    val scanner = Scanner(System.`in`)

    fun play() {
        val state = State(node)
        state.node.print()
        do {
            println("Your answer: ")
            val ans = scanner.nextLine()
            state.move(ans.toInt(), 'x')
            if(state.node.winner!='_') {
                println("Winner is ${state.node.winner.toUpperCase()}")
                return
            }
            if(state.node.child.isEmpty()) {
                println("Match Drawn")
            }
            state.automove('o')
            if(state.node.winner!='_') {
                println("Winner is ${state.node.winner.toUpperCase()}")
                return
            }
            state.node.print()

        } while (state.node.child.isNotEmpty())
        if(state.node.child.isEmpty()) {
            println("Match Drawn")
        }
    }
}

class State(var node: Node) {
    fun move(index: Int, player: Char): Unit =
        if(index<0 || index > 8) {

        } else if(node.str[index]!='_') {

        } else {
            val str = java.lang.StringBuilder(node.str)
            str[index] = player
            node = node.child.filter {
                it.str.equals(str)
            }.first()!!
        }

    fun automove(player: Char): Unit {
        val winningPaths = winningPaths(node, player)
        val answers = winningPaths.minOf { it.size }!!
        val answer = winningPaths.filter {
            it.size ==answers
        }.random()[0]

        node = node.child.filter {
            it.str == answer
        }.first()
    }

    fun winningPaths(node: Node, player: Char): List<List<String>> {
        if(node.child.isEmpty()) {
            return listOf()
        } else if(node.winner==player) {
            return listOf(listOf(node.str))
        } else if(node.winner!='_' && node.winner!=player) {
            return listOf()
        } else {
            return node.child.map {
                winningPaths(it, player)
            }.filter {
                it.isNotEmpty()
            }.map {
                it.map {
                    l->
                        val list = l.toMutableList()
                        list.add(0, node.str)
                        list.toList()
                }

            }.flatten()
        }
    }
}