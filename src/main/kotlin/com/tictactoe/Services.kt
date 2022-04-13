package com.tictactoe

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Services:CommandLineRunner {
    var running: Boolean = true
    var error: Boolean = false

    override fun run(vararg args: String?) {
        Thread {
            try {
                Board.makeTree()
            } catch (t:Throwable) {
                error = true
            }
            running = false
        }.start()
    }


}