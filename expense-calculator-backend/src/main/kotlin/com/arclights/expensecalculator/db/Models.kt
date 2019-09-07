package com.arclights.expensecalculator.db

import java.util.UUID

data class Person(
        val id: UUID?,
        val name: String
)

data class Card(
        val id: UUID?,
        val name: String,
        val comment: String
)

data class Category(
        val id: UUID?,
        val name: String,
        val comment: String
)