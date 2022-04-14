package com.tictactoe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.io.File
import kotlin.math.pow

@SpringBootApplication
@EnableWebMvc
class RicTacToeApplication

fun main(args: Array<String>) {
	runApplication<RicTacToeApplication>(*args)
//	println(2.0*(9.0)<4.0.pow(8))
//	load()
//	Game().play()
}
