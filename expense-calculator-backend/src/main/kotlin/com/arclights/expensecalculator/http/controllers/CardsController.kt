package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Card
import com.arclights.expensecalculator.db.CardDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
class CardsController(private val cardDao: CardDao) {
    @PutMapping(path = ["/card"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody()
    fun createUpdateCard(@RequestBody card: Card): Mono<Card> = cardDao.createUpdateCard(card)

    @GetMapping(path = ["/card"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCards(): Flux<Card> = cardDao.getCards()

    @GetMapping(path = ["/card/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCard(@PathVariable id: UUID): Mono<Card> = cardDao.getCard(id)
}