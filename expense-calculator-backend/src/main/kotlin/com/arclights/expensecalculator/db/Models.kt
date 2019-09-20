package com.arclights.expensecalculator.db

import java.math.BigDecimal
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

data class CardWithOwnership(
        val card: Card,
        val owners: List<Person>
)

data class CalculationListing(
        val year: Int,
        val month: Int
)

data class Calculation(
        val id: UUID?,
        val year: Int,
        val month: Int,
        val incomes: List<Income>,
        val expenses: List<Expense>,
        val personalExpenseCorrections: List<PersonalExpense>
)

data class Income(
        val amount: BigDecimal,
        val comment: String,
        val person: Person
)

data class Expense(
        val amount: BigDecimal,
        val comment: String,
        val card: Card
)

data class PersonalExpense(
        val person: Person,
        val corrections: List<PersonalExpenseCorrection>
)

data class PersonalExpenseCorrection(
        val id: UUID?,
        val amount: BigDecimal,
        val comment: String,
        val category: Category
)