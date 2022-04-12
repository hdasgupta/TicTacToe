package com.tictactoe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import java.io.File
import kotlin.math.pow

@SpringBootApplication
@PropertySource("classpath: tictactoe.properties")
class RicTacToeApplication

fun main(args: Array<String>) {
//	runApplication<RicTacToeApplication>(*args)
	Board.makeTree()
	writeToProperty(File("C:\\Users\\himag\\tictactoe.properties"))
//	println(3.0*(8.0)/8)
//	load()
//	Game().play()
}
