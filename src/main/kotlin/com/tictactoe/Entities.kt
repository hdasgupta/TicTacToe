package com.tictactoe

import com.tictactoe.Main.BoardUtils.Companion.string
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Objects
import java.util.TreeMap
import java.util.concurrent.LinkedBlockingQueue

@Component
class Queues {
//    @Autowired
//    private lateinit var queueRepo: QueueRepo
    val queues: LinkedBlockingQueue<Queue> = LinkedBlockingQueue()
    private var completed: MutableSet<Long> = mutableSetOf()
    @Autowired
    private lateinit var options: Options

//    @Transactional
    fun add(current: Long,
            player: Int,
            parent: Long? = null
    ) {
        if(!processed(current) && !queues.any { it.current==current }) {
            options[current] = parent
            queues.add(Queue(current = current, player = player, parent = parent))
        } else {
            if(parent == 528L) {
                println()
            }
            options[current] = parent
        }
    }

    fun processed(current: Long) = completed.contains(current)


//    @Transactional
    fun peek() = queues.peek()

    fun poll() {
        val queue = queues.poll()
        completed.add(queue.current)
    }


    fun isEmpty() = queues.isEmpty()

    fun isNotEmpty() = queues.isNotEmpty()

    fun pending() = queues.size

}

@Component
class Options {
    private val priorityMap: MutableMap<Long, Option?> = TreeMap()
//    @Autowired
//    private lateinit var optionRepo: OptionRepo

    val all:List<Option>
        get() = priorityMap.keys.sorted().map { priorityMap[it]!! }

    private fun add(option: Option) {
        priorityMap[option.id] = option
    }

    operator fun get(index: Long): Option? =
        if(priorityMap.containsKey(index)) {
            priorityMap[index]!!
        } else {
            null
        }

    operator fun get(index: Board): List<Option> =
        priorityMap.values.filter { it?.board?.id==index.id } as List<Option>


//    @Transactional
    operator fun set(index: Long, parent: Long?): Option =
        get(index)
            .let {
                if(it==null) {
                    var p:MutableSet<Option> = mutableSetOf()
                    if(parent!=null) {
                        val par = get(parent).let {
                            par->
                            par ?: Option(parent, board = null)

                        }
                        add(par)
                        p.add(par)
                    }
                    val o = Option(index, parent = p,  board = null)
                    add(o)
                    o
//                    optionRepo.save(o)
                } else {
                    if (parent != null) {
                        /*if (it!!.parent != null && it!!.parent!!.id != get(parent)!!.id) {
                            println("${index.string} ${it!!.parent!!.id.string}, ${get(parent)!!.id.string}")
                        }*/
                        it!!.parent.add(get(parent!!)!!)
                    }
//                    optionRepo.save(it)
                    it
                }
            }

//    fun saveAll() = optionRepo.saveAll(priorityMap.values)

}

@Component
class BoardMaster {
    private val priorityMap: MutableMap<Long, Board?> = TreeMap()
//    @Autowired
//    private lateinit var optionRepo: OptionRepo
//    @Autowired
//    private lateinit var boardRepo: BoardRepo
    @Autowired
    private lateinit var options: Options

    operator fun get(index: Long): Board? =
        if(priorityMap.containsKey(index)) {
            priorityMap[index]
        }else {
            null
        }

//    @Transactional
    operator fun set(index: Long, boardUtils: Main.BoardUtils): Unit {
        var list = mutableListOf<Option>()
        val existingBoards = boardUtils.values.map { options[it] }.filter { Objects.nonNull(it) }
        var board = if(existingBoards.isNotEmpty())
            existingBoards.first()?.board ?: Board(winner = boardUtils.winner?.toInt())
        else
            Board(winner = boardUtils.winner.let { it?.toInt() })
        list.addAll(
            boardUtils.values.map {
                if(options[it]==null) {
                    options[it] = null
                }
                options[it]!!.board = board
                options[it]!!
            }
        )
//        boardRepo.save(board)
//        optionRepo.saveAll(list)
        priorityMap[index] = board
    }

//    fun saveAll() = boardRepo.saveAll(priorityMap.values)
}

//@Entity
//@Table(name="queue")
class Queue (
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var current: Long,
    var player: Int,
    var parent: Long? = null
) {
    constructor(): this(-1L, Value.None.ordinal, null)
}
private var boardIndex = 0L
//@Entity
//@Table(name = "board")
class Board(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = boardIndex++,
    var winner: Int?,
) {
    constructor(): this(winner =  null)
}
//
//@Entity
//@Table(name = "options")
class Option(
//    @Id
    var id:Long,
//    @ManyToOne
//    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    var parent: MutableSet<Option> = mutableSetOf(),
//    @OneToOne
//    @JoinColumn(name = "board_id", referencedColumnName = "id")
    var board: Board?
) {
    constructor(): this(-1, mutableSetOf(), null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Option

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}

