package com.arclights.expensecalculator.service

import com.arclights.expensecalculator.db.CardOwnership
import com.arclights.expensecalculator.db.Person
import java.util.UUID
import com.arclights.expensecalculator.db.Card as dbCard

data class Card(
        val id: UUID?,
        val name: String,
        val comment: String,
        val owners: List<Person>
) {
    fun toDbCard() = dbCard(id, name, comment)

    companion object {
        fun fromDbModel(cardOwnership: CardOwnership) = with(cardOwnership) {
            Card(card.id, card.name, card.comment, owners)
        }
    }
}