package com.tictactoe.controller

import com.tictactoe.Board
import com.tictactoe.Services
import com.tictactoe.writeToProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

@Controller
class Controlers {
    @Autowired
    lateinit var services: Services

    @GetMapping(value = ["/"])
    fun index():String = "Index"

    @GetMapping(value = ["/download"])
    @ResponseBody
    fun download(responseBody: HttpServletResponse) {
        if(services.running) {
            responseBody.writer.println("Running: (Processed ${Board.processed})")
        } else if(services.error) {
            responseBody.writer.println("Error Occured")
        } else {
            writeToProperty(responseBody.outputStream)
        }
    }
}