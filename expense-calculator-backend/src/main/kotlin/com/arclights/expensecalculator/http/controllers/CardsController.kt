package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.service.Card
import com.arclights.expensecalculator.service.CardsService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping(path = ["/card"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CardsController(private val cardsService: CardsService) {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody()
    fun createUpdateCard(@RequestBody card: Card): Mono<Card> = cardsService.createUpdateCard(card)

    @GetMapping
    @ResponseBody
    fun getCards(): Flux<Card> = cardsService.getCards()

    @GetMapping(path = ["/{id}"])
    @ResponseBody
    fun getCard(@PathVariable id: UUID): Mono<Card> = cardsService.getCard(id)
}