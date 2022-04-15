package com.tictactoe

import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.setExactValue
import com.tictactoe.Main.BoardUtils.Companion.Flip.Companion.winner
import com.tictactoe.Main.BoardUtils.Companion.moves
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter


@SpringBootApplication
@EnableWebMvc
class RicTacToeApplication

fun main(args: Array<String>) {
	runApplication<RicTacToeApplication>(*args)

	val v =(0..8).map {  when("XXOOXXOOX"[it]) {
		'O'->Pair(it, 2L)
		'X' -> Pair(it, 1L)
		else ->Pair(it, 0L)
	} }.reduce { acc, pair -> Pair(0, setExactValue(acc.second, pair.first, pair.second)) }.second

//	v = setExactValue(v, 7, 1)
//	v = setExactValue(v, 8, 2)

	println(v)
	println(moves(v, Value.O))
	println (winner(v))
//	println(2.0*(9.0)<4.0.pow(8))
////	load()
//	Game().play()
}

@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurerAdapter() {
	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler(
			"/webjars/**",
			"/img/**",
			"/css/**",
			"/js/**"
		)
			.addResourceLocations(
				"classpath:/META-INF/resources/webjars/",
				"classpath:/static/img/",
				"classpath:/static/css/",
				"classpath:/static/js/"
			)
	}
}