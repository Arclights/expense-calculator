package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Person
import com.arclights.expensecalculator.db.PersonDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
class PersonController(private val personDao: PersonDao) {
    @PutMapping(path = ["/person"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdatePerson(@RequestBody person: Person): Mono<Person> = personDao.createOrUpdatePerson(person)

    @GetMapping(path = ["/person"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getPersons(): Flux<Person> = personDao.getAllPersons()

    @GetMapping(path = ["/person/:id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getPerson(@RequestParam id: UUID) = personDao.getPerson(id)
}