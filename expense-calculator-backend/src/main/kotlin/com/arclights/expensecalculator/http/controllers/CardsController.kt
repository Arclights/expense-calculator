package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Card
import com.arclights.expensecalculator.db.CardDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class CardsController(private val cardDao: CardDao) {
    @PutMapping(path = ["/card"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody()
    fun createUpdateCard() {
        TODO()
    }

    @GetMapping(path = ["/card"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCards(): Flux<Card> = cardDao.getCards()

    @GetMapping(path = ["/card/:id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCard() {
        TODO()
    }
}