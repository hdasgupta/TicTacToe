package com.tictactoe

import sun.reflect.generics.tree.Tree
import java.io.File
import java.io.OutputStream
import java.util.Properties
import java.util.Queue
import java.util.TreeMap
import java.util.concurrent.*
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

val map:TreeMap<UInt, Board> = TreeMap()
val parent: TreeMap<UInt, UInt> = TreeMap()

fun writeToProperty(o: OutputStream) {
    val p = Properties()

    parent.entries.forEach {

            i->p.setProperty("T${i.key}", "${map[i.key]!!.winner} ${i.value}")
    }
    p.save(o,"Tic Tac Toe")
}



fun load(): Node {

    val nodes: TreeMap<Int, Node> = TreeMap()

    val p = Properties()
    p.load(Board.javaClass.classLoader.getResourceAsStream("tictactoe.properties"))

    p.forEach {
        val child = it.key.toString().substring(1).toInt()
        val parentWinner = it.value.toString().split(" ")
        val winner = Value.values()[parentWinner[0].toInt()]
        //val childStr = Board.getList(child).joinToString("") { v->v.chr.toString() }
        //val childNode = Node(childStr, winner.chr, mutableListOf())
        //nodes[child] = childNode
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

class Board private constructor(val value: UInt) {
    val values = matches(value)

    val winner: UInt = winner(value)

    init {

        if(values.size>1) {
            println(values)
        }
    }



    companion object {

        var processed: Int = 0

        fun makeTree() {
            var list:LinkedBlockingQueue<List<UInt?>> = LinkedBlockingQueue()
            val oponentList = Value.values().map { it.opponent().ordinal.toUInt() }
            val mainService = Executors.newFixedThreadPool(Int.MAX_VALUE)
            val updateService = Executors.newFixedThreadPool(Int.MAX_VALUE)
            var running = true
            getBoard(0u)
            list.add(listOf(0u, Value.X.ordinal.toUInt(), null))

            Thread {
                while (running) {
                    Thread.sleep(5000)
                    System.gc()
                }
            }.start()
            while (list.isNotEmpty()) {
                 while (list.isEmpty()){}
                val i = list.poll()!!
                processed++
                 println(i)
                 if(map[i[0]]==null) {
                     println()
                 }
                if(map[i[0]]!!.winner == Value.None.ordinal.toUInt()) {
                    mainService.run {
                        val board = map[i[0]]!!
                        for(v in board.values) {
                            for(index in 0..8) {
                                if(getValue(v, index) == 0u) {
                                    val it = (v and inverts[index]!!) or (i[1]!! shl index)

                                    val b = getBoard(it)
                                    for(value in b.values) {
                                        list.add(listOf(value, oponentList[i[1]!!.toInt()], i[0]))
                                        parent[value] = i[0]!!
                                    }
                                }
                            }
                        }

                    }
                }
            }

            //mainService.awaitTermination(1,TimeUnit.HOURS )
            updateService.awaitTermination(1, TimeUnit.HOURS)
            running = false
        }

        fun getBoard(value: UInt): Board {
            return if(map[value] !=null) {
                map[value]!!
            } else {
                val b = Board(value)
                println(b.value)
                b.values.forEach {
                    map[it] = b
                }
                b
            }
        }

        private fun matches(value: UInt): Set<UInt> {
            val values = mutableSetOf<UInt>()
            var list = value
            (0..3).forEach { _ ->
                values.add(list)
                values.add(flipHorVal(list))
                values.add(flipHorVal(list))
                values.add(flipDiag1Val(list))
                values.add(flipDiag2Val(list))
                values.add(transpose(list))
                list = rotate(list)
            }
            return values.toSet()
        }

        private fun winner(value: UInt): UInt {
            val values:Set<UInt> = setOf()
            var list = value
            if((0..8).all { getExactValue(value, it) == Value.None.ordinal.toUInt() }) {
                return Value.None.ordinal.toUInt()
            }
            (0..1).forEach {
                (0..2).forEach {
                        row->
                    var value = isSameRow(list, row)
                    if(value != Value.None.ordinal.toUInt())
                        return value
                }
                list = rotate(list)
            }
            return Value.None.ordinal.toUInt()
        }

        private fun isSameRow(v: UInt, row: Int): UInt =
            if(getValue(v, row*3) ==getValue(v, row*3+1) &&
                getValue(v, row*3)==getValue(v, row*3+2))
                getExactValue(v, row*3)
            else
                Value.None.ordinal.toUInt()

        fun rotate(v: UInt): UInt = flipHor(flipDiag1(v))

        fun transpose(v: UInt): UInt = flipVert(flipHor(v))

        fun flipDiag1Val(v: UInt): UInt = flipDiag1(v)

        fun flipDiag2Val(v: UInt): UInt = flipDiag2(v)

        fun flipHorVal(v: UInt): UInt = flipHor(v)

        fun flipVertVal(v: UInt): UInt = flipVert(v)

        fun flipDiag1(v: UInt): UInt {

            return swap(swap(swap(v, 2, 6), 1, 3), 5, 7)
        }

        fun flipDiag2(v: UInt): UInt {
            return swap(swap(swap(v, 0, 8), 1, 5), 3, 7)
        }

        fun flipHor(v: UInt): UInt {
            return swap(swap(swap(v, 0, 2), 3, 5), 6, 8)
        }

        fun flipVert(v: UInt): UInt {
            return swap(swap(swap(v, 0, 6), 1, 7), 2, 8)
        }
//
//        fun getValue(v: List<Value>): Int =
//            v.map { it.ordinal }.reduce { a, o -> a*3 + o}

//        fun getList(v: Int): List<Value>  {
//            var value: Int = v
//            val list: MutableList<Value> = mutableListOf()
//            while(value>0) {
//                list.add(0, Value.values()[ value%3 ])
//                value /= 3
//            }
//            while(list.size<9) {
//                list.add(0, Value.values()[0])
//            }
//            return list
//        }

        private var shifts: Map<Int, UInt> = (0..8).map {
            mutableMapOf(Pair(it, 3u shl it*2))
        }.reduce {
                a, m -> a.let {
            it.putAll(m)
            it
        }
        }
        private var inverts: Map<Int, UInt> = shifts.entries.map {
            mutableMapOf(Pair(it.key, it.value.inv()))
        }.reduce {
                a, m -> a.let {
            it.putAll(m)
            it
        }
        }

        private fun getExactValue(value: UInt, index: Int): UInt = (value and shifts[index]!!) shr index*2

        private fun getValue(value: UInt, index: Int): UInt =
            if(index<0 || index>8) {
                println()
                throw Exception("error")
            } else {
                value and shifts[index]!!
            }

        private fun swap(values: UInt, i1:Int, i2: Int): UInt {
            val value1 = getExactValue(values, i1) shl i2*2
            val value2 =  getExactValue(values, i2) shl i1*2
            return (((values and inverts[i2]!! ) or value1) and inverts[i1]!! ) or value2
        }
    }



}