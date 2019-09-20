package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CARDS
import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record

fun mapCard(r: Record) = Card(r.get(CARDS.ID), r.get(CARDS.NAME), r.get(CARDS.COMMENT))

fun mapPerson(r: Record) = Person(r.get(PERSONS.ID), r.get(PERSONS.NAME))