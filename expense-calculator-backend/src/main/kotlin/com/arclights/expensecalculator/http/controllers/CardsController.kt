package com.arclights.expensecalculator.http.controllers

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CardsController {
    @PutMapping(path = ["/card"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody()
    fun createUpdateCard() {
        TODO()
    }

    @GetMapping(path = ["/card"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCards() {
        TODO()
    }

    @GetMapping(path = ["/card/:id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCard() {
        TODO()
    }
}