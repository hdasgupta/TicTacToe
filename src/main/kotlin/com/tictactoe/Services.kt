package com.tictactoe

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class Services:CommandLineRunner {
    var running: Boolean = true
    var error: Boolean = false
    @Autowired
    lateinit var main: Main

    override fun run(vararg args: String?) {
        Thread {
            try {
//                main.makeTree()
                //main.writeToProperty(File("C:\\Users\\himag\\tictactoe.properties").outputStream())
            } catch (t:Throwable) {
                t.printStackTrace()
                error = true
            }
            running = false
        }.start()
    }


}