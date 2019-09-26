package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CARDS
import com.arclights.expensecalculator.db.Tables.EXPENSES
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class ExpensesDao(private val dsl: DefaultDSLContext) {

    fun createUpdateExpenses(calculationId: UUID, expenses: List<Expense>): Flux<Expense> =
            Flux.mergeSequential(expenses.map { createOrUpdateExpense(calculationId, it) })

    fun createOrUpdateExpense(calculationId: UUID, expense: Expense): Mono<Expense> =
            getExpense(calculationId, expense.card)
                .flatMap { updateExpense(calculationId, expense) }
                .switchIfEmpty(Mono.defer { createExpense(calculationId, expense) })

    private fun createExpense(calculationId: UUID, expense: Expense): Mono<Expense> = Mono
        .fromCallable {
            dsl.insertInto(EXPENSES, EXPENSES.MONTHLY_CALCULATION_ID, EXPENSES.CARD_ID, EXPENSES.AMOUNT, EXPENSES.COMMENT)
                .values(calculationId, expense.card.id, expense.amount.toDouble(), expense.comment)
                .returning()
                .fetchOne()
        }
        .map { mapExpense(it) }

    private fun updateExpense(calculationId: UUID, expense: Expense): Mono<Expense> = Mono
        .fromCallable {
            dsl.update(EXPENSES)
                .set(EXPENSES.AMOUNT, expense.amount.toDouble())
                .set(EXPENSES.COMMENT, expense.comment)
                .where(EXPENSES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(EXPENSES.CARD_ID.eq(expense.card.id))
                .returning()
                .fetchOne()
        }
        .map { mapExpense(it) }

    fun getExpense(calculationId: UUID, card: Card): Mono<Expense> = Mono
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
}

private fun mapExpense(r: Record): Expense = Expense(
        r.get(EXPENSES.AMOUNT).toBigDecimal(),
        r.get(EXPENSES.COMMENT),
        mapCard(r)
)