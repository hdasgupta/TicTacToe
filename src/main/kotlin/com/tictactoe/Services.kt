package com.tictactoe

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Services:CommandLineRunner {
    var running: Boolean = true
        get() = running

    override fun run(vararg args: String?) {
        Thread {
            Board.makeTree()
            running = false
        }
    }


}