package com.arclights.expensecalculator.db

import org.jooq.Record

fun mapCard(r: Record) = Card(r.get(Tables.CARDS.ID), r.get(Tables.CARDS.NAME), r.get(Tables.CARDS.COMMENT))

fun mapPerson(r: Record) = Person(r.get(Tables.PERSONS.ID), r.get(Tables.PERSONS.NAME))