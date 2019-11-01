package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CARDS
import com.arclights.expensecalculator.db.Tables.EXPENSES
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class ExpensesDao(private val dsl: DefaultDSLContext) {

    @Transactional
    fun createUpdateExpenses(calculationId: CalculationId, expenses: List<Expense>): Flux<Pair<CalculationId, CardId>> {
        val incomingCardIds = expenses.map { it.card.id }.toSet()
        return getIdsForExistingExpenses(calculationId)
            .filter { incomingCardIds.contains(it.second).not() }
            .flatMap { deleteExpense(it.first, it.second) }
            .collectList()
            .thenMany(
                Flux.mergeSequential(expenses.map { createOrUpdateExpense(calculationId, it) })
            )
    }

    fun createOrUpdateExpense(calculationId: CalculationId, expense: Expense): Mono<Pair<CalculationId, CardId>> =
        getExpense(calculationId, expense.card)
            .flatMap { updateExpense(calculationId, expense) }
            .switchIfEmpty(Mono.defer { createExpense(calculationId, expense) })

    private fun createExpense(calculationId: CalculationId, expense: Expense): Mono<Pair<CalculationId, CardId>> = Mono
        .fromCallable {
            dsl.insertInto(
                EXPENSES,
                EXPENSES.MONTHLY_CALCULATION_ID,
                EXPENSES.CARD_ID,
                EXPENSES.AMOUNT,
                EXPENSES.COMMENT
            )
                .values(calculationId, expense.card.id, expense.amount.toDouble(), expense.comment)
                .returning(EXPENSES.MONTHLY_CALCULATION_ID, EXPENSES.CARD_ID)
                .fetchOne()
        }
        .map { it.monthlyCalculationId to it.cardId }

    private fun updateExpense(calculationId: CalculationId, expense: Expense): Mono<Pair<CalculationId, CardId>> = Mono
        .fromCallable {
            dsl.update(EXPENSES)
                .set(EXPENSES.AMOUNT, expense.amount.toDouble())
                .set(EXPENSES.COMMENT, expense.comment)
                .where(EXPENSES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(EXPENSES.CARD_ID.eq(expense.card.id))
                .returning(EXPENSES.MONTHLY_CALCULATION_ID, EXPENSES.CARD_ID)
                .fetchOne()
        }
        .map { it.monthlyCalculationId to it.cardId }

    private fun getIdsForExistingExpenses(calculationId: CalculationId): Flux<Pair<CalculationId, CardId>> = Flux
        .from(
            dsl.select(
                EXPENSES.MONTHLY_CALCULATION_ID,
                EXPENSES.CARD_ID
            )
                .from(EXPENSES)
                .where(EXPENSES.MONTHLY_CALCULATION_ID.eq(calculationId))
        )
        .map { it.get(EXPENSES.MONTHLY_CALCULATION_ID) to it.get(EXPENSES.CARD_ID) }

    fun getExpense(calculationId: CalculationId, card: Card): Mono<Expense> = Mono
        .from(
            dsl.select(
                EXPENSES.MONTHLY_CALCULATION_ID,
                EXPENSES.CARD_ID,
                EXPENSES.AMOUNT,
                EXPENSES.COMMENT,
                CARDS.ID,
                CARDS.NAME,
                CARDS.COMMENT
            )
                .from(EXPENSES)
                .leftJoin(CARDS).on(CARDS.ID.eq(EXPENSES.CARD_ID))
                .where(EXPENSES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(EXPENSES.CARD_ID.eq(card.id))
        )
        .map { mapExpense(it) }

    fun deleteExpense(calculationId: CalculationId, cardId: CardId): Mono<Expense> = Mono
        .fromCallable {
            dsl
                .deleteFrom(EXPENSES)
                .where(EXPENSES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(EXPENSES.CARD_ID.eq(cardId))
                .returning()
                .fetchOne()
        }
        .map { mapExpense(it) }
}

private fun mapExpense(r: Record): Expense = Expense(
    r.get(EXPENSES.AMOUNT).toBigDecimal(),
    r.get(EXPENSES.COMMENT),
    mapCard(r)
)