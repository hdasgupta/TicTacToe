package com.tictactoe

import org.springframework.stereotype.Repository
import java.util.*

//@Repository
//interface QueueRepo: CrudRepository<Queue, Long> {
//    fun findByCurrent(current:Long): Optional<Queue>
//    fun findFirst1ByProcessedFalseOrderByIdAsc(): Queue
//    fun countByProcessedFalse(): Long
//}

//@Repository
//interface BoardRepo: CrudRepository<Board, Long> {
//}
//
//@Repository
//interface OptionRepo: CrudRepository<Option, Long> {
//    fun findAllByBoardAndParentIsNull(board: Board): List<Option>
//    fun findAllByBoard(board: Board): List<Option>
//}