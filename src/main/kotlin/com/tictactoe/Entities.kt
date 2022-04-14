package com.tictactoe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.Objects
import java.util.TreeMap
import java.util.concurrent.LinkedBlockingQueue
import javax.persistence.*
import javax.transaction.Transactional

@Component
class Queues {
//    @Autowired
//    private lateinit var queueRepo: QueueRepo
    val queues: LinkedBlockingQueue<Queue> = LinkedBlockingQueue()
    private var completed: MutableSet<Long> = mutableSetOf()
    @Autowired
    private lateinit var options: Options

    @Transactional
    fun add(current: Long,
            player: Int,
            parent: Long? = null
    ) {
        if(!completed.contains(current)) {
            completed.add(current)
            options[current] = parent
            queues.add(Queue(current = current, player = player, parent = parent))
        } else {

        }
    }



    @Transactional
    fun poll() = queues.poll()

    fun peek() = queues.peek()


    fun isEmpty() = queues.isEmpty()

    fun isNotEmpty() = queues.isNotEmpty()

    fun pending() = queues.size

}

@Component
class Options {
    private val priorityMap: MutableMap<Long, Option?> = TreeMap()
//    @Autowired
//    private lateinit var optionRepo: OptionRepo

    val all
        get() = priorityMap.values

    operator fun get(index: Long): Option? =
        if(priorityMap.containsKey(index)) {
            priorityMap[index]!!
        } else {
            null
        }

    operator fun get(index: Board): List<Option> =
        priorityMap.values.filter { it?.board?.id==index.id } as List<Option>

    @Transactional
    operator fun set(index: Long, parent: Long?): Option =
        get(index)
            .let {
                if(it==null) {
                    var p:Option? = null
                    if(parent!=null) {
                        p = get(parent).let {
                            par->
                            par ?: Option(parent, board = null)

                        }
                        priorityMap[parent] = p
                    }
                    val o = Option(index, parent = p,  board = null)
                    priorityMap[index] = o
                    o
//                    optionRepo.save(o)
                } else {
                    if (parent != null)
                        it!!.parent = get(parent)
                    else
                        it!!.parent = null
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

    @Transactional
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
    var id:Long? = null,
    var current: Long,
    var player: Int,
    var parent: Long? = null,
    var processed: Boolean = false
) {
    constructor(): this(null, 0L, Value.X.ordinal, null)
}

//@Entity
//@Table(name = "board")
class Board(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long? = null,
    var winner: Int?,
) {
    constructor(): this(null, Value.None.ordinal)
}
//
//@Entity
//@Table(name = "options")
class Option(
//    @Id
    var id:Long?,
//    @ManyToOne
//    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    var parent: Option? = null,
//    @OneToOne
//    @JoinColumn(name = "board_id", referencedColumnName = "id")
    var board: Board?
) {
    constructor(): this(null, null, null)
}

