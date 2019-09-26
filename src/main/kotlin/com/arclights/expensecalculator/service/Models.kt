package com.arclights.expensecalculator.service

import com.arclights.expensecalculator.db.CardWithOwnership
import com.arclights.expensecalculator.db.Category
import com.arclights.expensecalculator.db.Person
import java.math.BigDecimal
import java.util.UUID
import com.arclights.expensecalculator.db.Card as dbCard

data class CardWithOwnership(
        val id: UUID?,
        val name: String,
        val comment: String,
        val owners: List<Person>
) {
    fun toDbCard() = dbCard(id, name, comment)

    companion object {
        fun fromDbModel(cardWithOwnership: CardWithOwnership) = with(cardWithOwnership) {
            CardWithOwnership(card.id, card.name, card.comment, owners)
        }
    }
}

data class Card(
        val id: UUID?,
        val name: String,
        val comment: String
)

data class Calculation(
        val id: UUID?,
        val year: Int,
        val month: Int,
        val personalCalculations: List<PersonCalculation>,
        val expenses: List<Expense>
)

data class PersonCalculation(
        val person: Person,
        val income: Income,
        val expenseCorrections: List<PersonalExpenseCorrection>
)

data class Income(
        val amount: BigDecimal,
        val comment: String,
        val person: Person
)

data class PersonalExpenseCorrection(
        val id: UUID?,
        val amount: BigDecimal,
        val comment: String,
        val category: Category
)

data class Expense(
        val amount: BigDecimal,
        val comment: String,
        val card: Card
)
