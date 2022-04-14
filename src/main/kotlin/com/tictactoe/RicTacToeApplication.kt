package com.tictactoe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.winner
import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.setExactValue
import java.io.File
import kotlin.math.pow

@SpringBootApplication
@EnableWebMvc
class RicTacToeApplication

fun main(args: Array<String>) {
	runApplication<RicTacToeApplication>(*args)

//	val v = setExactValue(setExactValue(setExactValue(0, 0,2), 1, 2), 2, 2)
//	println(v)
//	println (winner(v))
//	println(2.0*(9.0)<4.0.pow(8))
//	load()
//	Game().play()
}
