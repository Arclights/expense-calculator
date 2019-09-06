package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.impl.DefaultDSLContext

class PersonDao(val dsl: DefaultDSLContext) {

    fun getAllPersons() {
        dsl.select(PERSONS.ID, PERSONS.NAME)
                .from(PERSONS)
    }
}