package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class PersonDao(val dsl: DefaultDSLContext) {

    fun getAllPersons(): Flux<Person> =
            Flux
                .from(
                        dsl.select(PERSONS.ID, PERSONS.NAME)
                            .from(PERSONS)
                )
                .map { r -> Person(r.get(PERSONS.ID), r.get(PERSONS.NAME)) }
}