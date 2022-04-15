package com.tictactoe.controller

import com.tictactoe.Game
import com.tictactoe.Queues
import com.tictactoe.Services
import com.tictactoe.State
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class Controlers {
    @Autowired
    lateinit var services: Services

    @Autowired
    lateinit var queues: Queues

    @Autowired
    lateinit var game: Game

    @GetMapping(value = ["/"])
    fun index():String = "Index"

    @GetMapping(value = ["/tictactoe"])
    fun tictactoe(player: Char?, move:Int?, map: ModelMap, session: HttpSession):String {
        val choosedPlayer = (player ?: 'x').toUpperCase()
        val opponent = if(choosedPlayer=='X') 'O' else 'X'
        var state: State
        if(move==null || move<0 ||move>8 || session.getAttribute("state") == null) {
            state = State(game.node)
            session.setAttribute("state", state)
        } else {
            state = session.getAttribute("state") as State
            val status = game.move(state, move)
            if(status.winner=='_') {
                state = State(game.node)
                session.setAttribute("state", state)
            }
            when(status.winner) {
                'X' -> map["message"] = "You Won"
                'O' -> map["message"] = "You Loss"
                '_' -> map["message"] = "Match Drawn"
            }
        }
        val charList = state.node.str.map {
            when(it) {
                'X'-> choosedPlayer
                'O'-> opponent
                else -> it
            }
        }.map {
            when(it) {
                'X'-> 1
                'O'-> 2
                else -> 0
            }
        }

        map["player"] = choosedPlayer
        map["output"] = arrayOf(
            charList.subList(0, 3),
            charList.subList(3, 6),
            charList.subList(6, 9)
        )
        map["chars"] = arrayOf( "?", 'X', 'O')
        map["classes"] = arrayOf("secondary", "primary", "success")

        return "tictactoe"
    }

    @GetMapping(value = ["/download"])
    @ResponseBody
    fun download(responseBody: HttpServletResponse) {
        if(services.running) {
            responseBody.writer.println("Running: (Processed ${services.main.processed}, pending ${queues.pending()})")
        } else if(services.error) {
            responseBody.writer.println("Error Occured")
        } else {
            services.main.writeToProperty(responseBody.outputStream)
        }
    }
}