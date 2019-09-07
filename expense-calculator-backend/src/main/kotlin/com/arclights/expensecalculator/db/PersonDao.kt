package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record2
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class PersonDao(val dsl: DefaultDSLContext) {

    fun getAllPersons(): Flux<Person> = Flux
        .from(
                dsl.select(PERSONS.ID, PERSONS.NAME)
                    .from(PERSONS)
        )
        .map(this::mapPerson)

    fun getPerson(id: UUID): Mono<Person> = Mono
        .from(
                dsl.select(PERSONS.ID, PERSONS.NAME)
                    .from(PERSONS)
                    .where(PERSONS.ID.eq(id))
        )
        .map(this::mapPerson)


    fun createOrUpdatePerson(person: Person): Mono<Person> =
            if (person.id == null) {
                createPerson(person)
            } else {
                updatePerson(person)
            }

    private fun updatePerson(person: Person): Mono<Person> = Mono
        .from(
                dsl.update(PERSONS)
                    .set(PERSONS.NAME, person.name)
                    .where(PERSONS.ID.eq(person.id))
        )
        .thenReturn(person)

    private fun createPerson(person: Person): Mono<Person> = Mono
        .fromCallable {
            dsl.insertInto(PERSONS, PERSONS.NAME)
                .values(person.name)
                .returning()
                .fetchOne()
        }
        .map { pr -> Person(pr.id, pr.name) }

    private fun mapPerson(r: Record2<UUID, String>): Person = Person(r.get(PERSONS.ID), r.get(PERSONS.NAME))
}