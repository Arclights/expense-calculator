package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Person
import com.arclights.expensecalculator.db.PersonDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping(path = ["/persons"], produces = [MediaType.APPLICATION_JSON_VALUE])
class PersonController(private val personDao: PersonDao) {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdatePerson(@RequestBody person: Person): Mono<Person> = personDao.createOrUpdatePerson(person)

    @GetMapping
    @ResponseBody
    fun getPersons(): Flux<Person> = personDao.getAllPersons()

    @GetMapping(path = ["/{id}"])
    @ResponseBody
    fun getPerson(@PathVariable id: UUID) = personDao.getPerson(id)
}