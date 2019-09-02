package com.arclights.expensecalculator.http

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController("calculation")
class CalculationController {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCalculation() {
        TODO()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCalculation() {
        TODO()
    }
}

@RestController("category")
class CategoriesController {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCategory() {
        TODO()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategories() {
        TODO()
    }

    @GetMapping(path = [":id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategory() {
        TODO()
    }
}

@RestController("card")
class CardsController {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCard() {
        TODO()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCards() {
        TODO()
    }

    @GetMapping(path = [":id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCard() {
        TODO()
    }
}

@RestController("person")
class PersonController {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdatePerson() {
        TODO()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getPersons() = Flux.

    @GetMapping(path = [":id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getPerson() {
        TODO()
    }
}