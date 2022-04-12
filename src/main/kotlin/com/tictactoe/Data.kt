package com.tictactoe

import sun.reflect.generics.tree.Tree
import java.io.File
import java.util.Properties
import java.util.TreeMap
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.streams.toList

enum class Value(val chr:Char) {
    None('_'),
    X('X'),
    O('O'),;
    fun opponent(): Value =
        if(this==None)
            None
        else if(this==X)
            O
        else
            X
}

val map:TreeMap<Int, Board> = TreeMap()
val parent: TreeMap<Int, Int> = TreeMap()

fun writeToProperty(f: File) {
    val p = Properties()

    parent.entries.forEach {

            i->p.setProperty("T${i.key}", "${map[i.key]!!.winner} ${i.value}")
    }
    p.save(f.outputStream(),"Tic Tac Toe")
}



fun load(): Node {

    val nodes: TreeMap<Int, Node> = TreeMap()

    val p = Properties()
    p.load(Board.javaClass.classLoader.getResourceAsStream("tictactoe.properties"))

    p.forEach {
        val child = it.key.toString().substring(1).toInt()
        val parentWinner = it.value.toString().split(" ")
        val winner = Value.values()[parentWinner[0].toInt()]
        val childStr = Board.getList(child).joinToString("") { v->v.chr.toString() }
        val childNode = Node(childStr, winner.chr, mutableListOf())
        nodes[child] = childNode
//        }
    }

    p.forEach {
        val child = it.key.toString().substring(1).toInt()
        val parentWinner = it.value.toString().split(" ")
        val childNode = nodes[child]!!
        val parent = parentWinner[1].toInt()

        nodes[parent]!!.child.add(childNode)
    }

    return nodes[0]!!
}

class Node(val str: String, val winner: Char, val child: MutableList<Node>) {
    fun print() {
        var i =0
        do {
            if(i%3==0)
                println("-------------")
            print("| ")
            print(
                when(str[i]){
                    '_'-> "$i "
                    else-> "${str[i]} "
                })
            if(i%3==2)
                println("|")
            i++
        } while (i<9)
        println("-------------")
    }
}

class Board private constructor(value: Int) {
    val values = matches(value)

    val winner: Int = winner(value)

    init {
        values.forEach { _ ->
            map[value] = this
        }
        if(values.size>1) {
            println(values)
        }
    }

    fun optionsFor(value: Value): List<Int> =
        values.map { getList(it) } .map {
            it.indices.filter { i->it[i]==Value.None }.map {
                i->
                val list = it.toMutableList()
                list[i] = value
                list
            }
        }.flatten().map { getValue(it) }


    companion object {

        fun makeTree() {
            var list = listOf(0)
            val mainThread = Executors.newFixedThreadPool(10000)
            val service = Executors.newFixedThreadPool(10000)
            Board(0)
            var current = Value.X
            (0..8).forEach { _ ->
                list = list.parallelStream().filter {
                    i-> map[i]!!.winner == Value.None.ordinal
                }.map {
                    i->
                    mainThread.run {
                        val l = map[i]!!.optionsFor(current)
                        l.forEach {
                            service.run {
                                val b = getBoard(it)
                                b.values.forEach { value ->
                                    parent[value] = i
                                }
                            }

                        }
                        l

                    }
                }.toList().flatten()
                current = current.opponent()
            }
        }

        fun getBoard(value: Int): Board {
            return if(map[value] !=null) {
                map[value]!!
            } else {
                Board(value)
            }
        }

        private fun matches(value: Int): Set<Int> {
            val values = mutableSetOf<Int>()
            var list = getList(value)
            (0..3).forEach { _ ->
                values.add(getValue(list))
                values.add(flipHorVal(list))
                values.add(flipHorVal(list))
                values.add(flipDiag1Val(list))
                values.add(flipDiag2Val(list))
                values.add(transpose(list))
                list = rotate(list)
            }
            return values.toSet()
        }

        private fun winner(value: Int): Int {
            val values:Set<Int> = setOf()
            var list = getList(value)
            (0..1).forEach {
                (0..2).forEach {
                        row->
                    var value = isSameRow(list, row)
                    if(value != Value.None)
                        return value.ordinal
                }
                list = rotate(list)
            }
            return Value.None.ordinal
        }

        private fun isSameRow(v: List<Value>, index: Int): Value =
            if(v[index*3]==v[index*3+1] && v[index*3]==v[index*3+2])
                v[index*3]
            else
                Value.None

        fun rotate(v: List<Value>): List<Value> = flipHor(flipDiag1(v))

        fun transpose(v: List<Value>): Int = getValue(flipVert(flipHor(v)))

        fun flipDiag1Val(v: List<Value>): Int = getValue(flipDiag1(v))

        fun flipDiag2Val(v: List<Value>): Int = getValue(flipDiag2(v))

        fun flipHorVal(v: List<Value>): Int = getValue(flipHor(v))

        fun flipVertVal(v: List<Value>): Int = getValue(flipVert(v))

        fun flipDiag1(v: List<Value>): List<Value> {
            val values = v.toMutableList()
            swap(values, 2, 6)
            swap(values, 1, 3)
            swap(values, 5, 7)
            return values
        }

        fun flipDiag2(v: List<Value>): List<Value> {
            val values = v.toMutableList()
            swap(values, 0, 8)
            swap(values, 1, 5)
            swap(values, 3, 7)
            return values
        }

        fun flipHor(v: List<Value>): List<Value> {
            val values = v.toMutableList()
            swap(values, 0, 2)
            swap(values, 3, 5)
            swap(values, 6, 8)
            return values
        }

        fun flipVert(v: List<Value>): List<Value> {
            val values = v.toMutableList()
            swap(values, 0, 6)
            swap(values, 1, 7)
            swap(values, 2, 8)
            return values
        }

        fun getValue(v: List<Value>): Int =
            v.map { it.ordinal }.reduce { a, o -> a*3 + o}

        fun getList(v: Int): List<Value>  {
            var value: Int = v
            val list: MutableList<Value> = mutableListOf()
            while(value>0) {
                list.add(0, Value.values()[ value%3 ])
                value /= 3
            }
            while(list.size<9) {
                list.add(0, Value.values()[0])
            }
            return list
        }


        private fun swap(values: MutableList<Value>, i1:Int, i2: Int) {
            val value = values[i2]
            values[i2] = values[i1]
            values[i1] = value
        }
    }



}