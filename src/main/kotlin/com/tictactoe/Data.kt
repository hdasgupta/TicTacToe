package com.tictactoe

import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.getExactValue
import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.setExactValue
import com.tictactoe.Main.BoardUtils.Companion.moves
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.util.Properties
import java.util.TreeMap
import java.util.concurrent.Executors

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




fun load(): Node {

    val nodes: TreeMap<Long, Node> = TreeMap()

    val p = Properties()
    p.load(Main.BoardUtils.javaClass.classLoader.getResourceAsStream("tictactoe.properties"))
    //nodes[0L] = Node("_________", null, mutableListOf())

    p.forEach {
        val child = it.key.toString().substring(1).toLong()
        val parentWinner = it.value.toString().split(" ")
        val winner = if(parentWinner[0]=="null") null else Value.values()[parentWinner[0].toInt()]
        val childStr = (0..8).joinToString("") { i->Value.values()[getExactValue(child, i).toInt()].chr.toString() }
        println(childStr)
        val childNode = Node(childStr, winner?.chr ?: null, mutableSetOf(),  mutableListOf())
        nodes[child] = childNode
    }

    p.forEach {
        val child = it.key.toString().substring(1).toLong()
        val parentWinner = it.value.toString().split(" ")
        //println(child)
        val childNode = nodes[child]!!
        val parents = if(parentWinner[1].isEmpty()) listOf() else parentWinner[1].split(",").map { i->i.toLong() }

        parents.forEach {
            parent->
            if(parent!=-1L) {
                nodes[parent]!!.child.add(childNode)
                childNode.parent.add(nodes[parent]!!)
            }
        }

    }
    nodes[0]!!.nearestWin()
    return nodes[0]!!
}



class Node(val str: String, val winner: Char?, val parent:MutableSet<Node>, val child: MutableList<Node>) {
    private var smartest: MutableMap<Char, Set<Node>> = mutableMapOf()
    val player = if(str.count { it=='X' }==str.count{ it == 'O'}) 'X' else 'O'
    var oWinsWithSteps: Int = Int.MAX_VALUE
    var oLoseWithSteps:Int = Int.MAX_VALUE
    var xWinningPossibilities : Set<Node> = setOf()
    var drawPossibilities: Set<Node> = setOf()
    var oWiningPossibilities: Set<Node> = setOf()

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node

        if (str != other.str) return false

        return true
    }

    override fun hashCode(): Int {
        return str.hashCode()
    }

    fun nearest(x: Set<Node>, o: Set<Node>, d: Set<Node>) {
        smartest['X'] = x.plus(d)
        smartest['O'] = o.plus(d)
        smartest['_'] = d
    }

    fun nearest(player:Char) = smartest[player]

    fun smartMove(): Set<Node> {
        return smartest[player]!!
    }

    fun nearestWin() {
        if(child.isEmpty()) {
            if(winner == 'X') {
                oWinsWithSteps = Int.MAX_VALUE
                oLoseWithSteps = 1
                xWinningPossibilities = setOf(this)
                drawPossibilities = setOf()
                oWiningPossibilities = setOf()

            } else if(winner == 'O') {
                oWinsWithSteps = 1
                oLoseWithSteps = Int.MAX_VALUE
                xWinningPossibilities = setOf()
                drawPossibilities = setOf()
                oWiningPossibilities = setOf(this)

            } else {
                oWinsWithSteps = Int.MAX_VALUE
                oLoseWithSteps = Int.MAX_VALUE
                xWinningPossibilities = setOf()
                drawPossibilities = setOf(this)
                oWiningPossibilities = setOf()
            }
        } else  {
            child.forEach{it.nearestWin()}
            oWinsWithSteps = child.minOf { it.oWinsWithSteps }
            oLoseWithSteps = child.minOf { it.oLoseWithSteps }
            if(oWinsWithSteps!= Int.MAX_VALUE) {
                oWinsWithSteps+=1
            }
            if(oLoseWithSteps!= Int.MAX_VALUE) {
                oLoseWithSteps+=1
            }
            xWinningPossibilities = child.map { it.xWinningPossibilities }.flatten().toSet()
            drawPossibilities = child.map { it.drawPossibilities }.flatten().toSet()
            oWiningPossibilities = child.map { it.oWiningPossibilities }.flatten().toSet()
        }
    }

    override fun toString(): String {
        return "string(0)"
    }

//    fun string(level: Int): String{
//        return "${"\t".repeat(level)}$str ($smartest) \n${child.joinToString("\n") { "${it.string(level+1)}" }}"
//    }
}
@Component
class Main {
    @Autowired
    lateinit var map: BoardMaster
    @Autowired
    lateinit var list: Queues
    @Autowired
    lateinit var options: Options
//    @Autowired
//    lateinit var optionRepo: OptionRepo

    var processed: Int = 0

    fun writeToProperty(o: OutputStream) {
        val p = Properties()

        options.all.forEach {

                i->p.setProperty("T${i!!.id}", "${i.board!!.winner} ${i.parent.joinToString(",") { it.id.toString() } }")
        }
        p.save(o,"Tic Tac Toe")
    }

    fun makeTree() {
        val oponentList = Value.values().map { it.opponent().ordinal.toLong() }
        var running = true
        val services = Executors.newFixedThreadPool(Int.MAX_VALUE)
        val b = getBoard(0)
        list.add(0, Value.X.ordinal, null)
        options[0]!!.board = map[0]

        Thread {
            while (running) {
                Thread.sleep(60000)
                System.gc()
            }
        }.start()
        while (list.isNotEmpty()) {
            val i = list.peek()!!
            processed++
//            println(i)
//            if(map[i.current.toLong()]==null) {
//                println()
//            }
            services.run {
                val b = getBoard(i.current)
//                println(map[i.current]!!.winner)
                if(map[i.current]!!.winner == null || map[i.current]!!.winner == Value.None.ordinal) {
                    val board = map[i.current]!!
                    for(v in options[board]) {

                        v.id!![Value.values()[i.player]].forEach {
                            if(v.id==131337L) {
                                println()
                            }

                            //println("${v.id!!.string} -> ${it.string}")
                            list.add(it, oponentList[i.player!!.toInt()].toInt(), v.id)


                        }
                    }

                } else {
                    //println(map[i.current]!!.winner)
                }


            }
            list.poll()
        }

//        map.saveAll()
//        options.saveAll()
        running = false
    }

    operator fun Long.get(value: Value): List<Long> {
        return moves(this, value)

    }

     fun getBoard(value: Long): BoardUtils {
        return if(map[value] !=null) {
            BoardUtils(value, options[map[value]!!].map { it.id }.toSet() as Set<Long>)
        } else {
            val b = BoardUtils(value)
            b.values.forEach {
                map[it] = b

            }
            b
        }
    }


    operator fun Long.get(index: Int) = getExactValue(this, index)


    class BoardUtils(value: Long, val values: Set<Long>) {

        constructor(value: Long): this(value, matches(value))

        val winner = value.winner
//        init {
//
//            if(values.size>1) {
//                println(values)
//            }
//        }


        companion object {

            operator fun Long.get(index: Int) = getExactValue(this, index)

            private val Long.flip
                get() = Flip(this)
            val Long.rotate
                get() = Flip.rotate(this)
            val Long.winner: Long?
                get() = Flip.winner(this)
            val Long.string
                get() =
                    (0..8).joinToString("") {
                            i->
                        Value.values()[getExactValue(this, i).toInt()].chr.toString()
                    }

            class Flip(value:Long) {
                val horizontal = flipHorVal(value)
                val vertical = flipVertVal(value)
                val diagonal1 = flipDiag1(value)
                val diagonal2 = flipDiag2(value)
                val transpose = transpose(value)
                companion object {

                    fun rotate(v: Long): Long = flipHor(flipDiag1(v))

                    private fun transpose(v: Long): Long = flipVert(flipHor(v))

                    private fun flipDiag1Val(v: Long): Long = flipDiag1(v)

                    private fun flipDiag2Val(v: Long): Long = flipDiag2(v)

                    private fun flipHorVal(v: Long): Long = flipHor(v)

                    private fun flipVertVal(v: Long): Long = flipVert(v)

                    private fun flipDiag1(v: Long): Long {

                        return swap(swap(swap(v, 2, 6), 1, 3), 5, 7)
                    }

                    private fun flipDiag2(v: Long): Long {
                        return swap(swap(swap(v, 0, 8), 1, 5), 3, 7)
                    }

                    private fun flipHor(v: Long): Long {
                        return swap(swap(swap(v, 0, 2), 3, 5), 6, 8)
                    }

                    private fun flipVert(v: Long): Long {
                        return swap(swap(swap(v, 0, 6), 1, 7), 2, 8)
                    }


                    fun winner(value: Long): Long? {
                        var list = value

                        (0..1).forEach {
                            (0..2).forEach {
                                    row->
                                var value = isSameRow(list, row)
                                if(value != Value.None.ordinal.toLong())
                                    return value
                            }
                            if(list[0]==list[4]&& list[0]==list[8] && list[0]!= Value.None.ordinal.toLong()) {
                                return list[0]
                            }
                            list = list.rotate
                        }
                        if((0..8).all { getExactValue(value, it) != Value.None.ordinal.toLong() }) {
                            return Value.None.ordinal.toLong()
                        }
                        return null
                    }

                    private fun isSameRow(v: Long, row: Int): Long =
                        if(v[row*3] ==v[row*3+1] &&
                            v[row*3]==v[row*3+2])
                            v[row*3]
                        else
                            Value.None.ordinal.toLong()

                    private var shifts: Map<Int, Long> = (0..8).map {
                        mutableMapOf(Pair(it, 3L shl it*2))
                    }.reduce {
                            a, m -> a.let {
                        it.putAll(m)
                        it
                    }
                    }
                    private var inverts: Map<Int, Long> = shifts.entries.map {
                        mutableMapOf(Pair(it.key, it.value.inv()))
                    }.reduce {
                            a, m -> a.let {
                        it.putAll(m)
                        it
                    }
                    }

                    fun getExactValue(value: Long, index: Int): Long = (value and shifts[index]!!) shr index*2

                    fun setExactValue(value: Long, index: Int, v: Long): Long = (value and inverts[index]!! ) or (v shl index*2)

                    private fun getValue(value: Long, index: Int): Long =
                        if(index<0 || index>8) {
                            println()
                            throw Exception("error")
                        } else {
                            value and shifts[index]!!
                        }

                    private fun swap(values: Long, i1:Int, i2: Int): Long =
                        setExactValue(setExactValue(values,i2, values[i1]), i1,  values[i2])
                }

            }

            fun moves(v: Long, value: Value) =
                (0..8).filter { v[it] == 0L}.map { setExactValue(v, it,value.ordinal.toLong()) }

            private fun matches(value: Long): Set<Long> {
                val values = mutableSetOf<Long>()
                var current = value
                (0..3).forEach { _ ->
                    values.add(current)
                    values.add(current.flip.horizontal)
                    values.add(current.flip.vertical)
                    values.add(current.flip.diagonal1)
                    values.add(current.flip.diagonal2)
                    values.add(current.flip.transpose)
                    current = current.rotate
                }
                return values.toSet()
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



        }


    }

}


