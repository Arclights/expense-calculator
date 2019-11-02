package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CATEGORIES
import com.arclights.expensecalculator.db.Tables.PERSONAL_EXPENSE_CORRECTIONS
import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class PersonalExpensesDao(private val dsl: DefaultDSLContext) {

    fun createUpdatePersonalExpenses(
        calculationId: CalculationId,
        personalExpenses: List<PersonalExpense>
    ): Flux<PersonalExpenseCorrectionId> =
        Flux.fromIterable(personalExpenses).flatMap { createOrUpdatePersonalExpense(calculationId, it) }

    fun createOrUpdatePersonalExpense(
        calculationId: CalculationId,
        personalExpense: PersonalExpense
    ): Flux<PersonalExpenseCorrectionId> =
        deleteNotIncludedEntries(calculationId, personalExpense)
            .thenMany(Flux.fromIterable(personalExpense.corrections))
            .flatMap {
                createOrUpdatePersonalExpenseCorrection(
                    calculationId,
                    personalExpense.person.id!!,
                    it
                )
            }

    private fun deleteNotIncludedEntries(
        calculationId: CalculationId,
        personalExpense: PersonalExpense
    ): Flux<PersonalExpenseCorrectionId> {
        val incomingIds = personalExpense.corrections.mapNotNull { it.id }.toSet()
        return getIdsForExpenseCorrectionsInCalculation(calculationId)
            .filter { incomingIds.contains(it).not() }
            .flatMap(this::delete)
    }

    private fun createOrUpdatePersonalExpenseCorrection(
        calculationId: CalculationId,
        personId: PersonId,
        personalExpenseCorrection: PersonalExpenseCorrection
    ): Mono<PersonalExpenseCorrectionId> =
        if (personalExpenseCorrection.id == null)
            createPersonalExpenseCorrection(calculationId, personId, personalExpenseCorrection)
        else
            updatePersonalExpenseCorrection(personalExpenseCorrection)

    private fun createPersonalExpenseCorrection(
        calculationId: CalculationId,
        personId: PersonId,
        personalExpenseCorrection: PersonalExpenseCorrection
    ): Mono<PersonalExpenseCorrectionId> = Mono
        .fromCallable {
            dsl.insertInto(
                PERSONAL_EXPENSE_CORRECTIONS,
                PERSONAL_EXPENSE_CORRECTIONS.MONTHLY_CALCULATION_ID,
                PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID,
                PERSONAL_EXPENSE_CORRECTIONS.AMOUNT,
                PERSONAL_EXPENSE_CORRECTIONS.COMMENT,
                PERSONAL_EXPENSE_CORRECTIONS.CATEGORY_ID
            )
                .values(
                    calculationId,
                    personId,
                    personalExpenseCorrection.amount.toDouble(),
                    personalExpenseCorrection.comment,
                    personalExpenseCorrection.category.id
                )
                .returning(PERSONAL_EXPENSE_CORRECTIONS.ID)
                .fetchOne()
        }
        .map { it.id }

    private fun updatePersonalExpenseCorrection(personalExpenseCorrection: PersonalExpenseCorrection): Mono<PersonalExpenseCorrectionId> =
        Mono
            .fromCallable {
                dsl.update(PERSONAL_EXPENSE_CORRECTIONS)
                    .set(PERSONAL_EXPENSE_CORRECTIONS.AMOUNT, personalExpenseCorrection.amount.toDouble())
                    .set(PERSONAL_EXPENSE_CORRECTIONS.COMMENT, personalExpenseCorrection.comment)
                    .where(PERSONAL_EXPENSE_CORRECTIONS.ID.eq(personalExpenseCorrection.id))
                    .returning(PERSONAL_EXPENSE_CORRECTIONS.ID)
                    .fetchOne()
            }
            .map { it.id }

    private fun getIdsForExpenseCorrectionsInCalculation(calculationId: CalculationId): Flux<PersonalExpenseCorrectionId> =
        Flux
            .from(
                dsl.select(PERSONAL_EXPENSE_CORRECTIONS.ID)
                    .from(PERSONAL_EXPENSE_CORRECTIONS)
                    .where(PERSONAL_EXPENSE_CORRECTIONS.MONTHLY_CALCULATION_ID.eq(calculationId))
            )
            .map { it.get(PERSONAL_EXPENSE_CORRECTIONS.ID) }

    fun delete(personalExpenseCorrectionId: PersonalExpenseCorrectionId): Mono<PersonalExpenseCorrectionId> =
        Mono
            .fromCallable {
                dsl.deleteFrom(PERSONAL_EXPENSE_CORRECTIONS)
                    .where(PERSONAL_EXPENSE_CORRECTIONS.ID.eq(personalExpenseCorrectionId))
                    .returning(PERSONAL_EXPENSE_CORRECTIONS.ID)
                    .fetchOne()
            }
            .map { it.id }

    fun getPersonalExpense(calculationId: CalculationId, personId: PersonId): Mono<PersonalExpense> = Flux
        .from(
            dsl.select(
                PERSONAL_EXPENSE_CORRECTIONS.ID,
                PERSONAL_EXPENSE_CORRECTIONS.MONTHLY_CALCULATION_ID,
                PERSONAL_EXPENSE_CORRECTIONS.AMOUNT,
                PERSONAL_EXPENSE_CORRECTIONS.COMMENT,
                PERSONS.ID,
                PERSONS.NAME,
                CATEGORIES.ID,
                CATEGORIES.NAME,
                CATEGORIES.COMMENT
            )
                .from(PERSONAL_EXPENSE_CORRECTIONS)
                .leftJoin(PERSONS).on(PERSONS.ID.eq(PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID))
                .leftJoin(CATEGORIES).on(CATEGORIES.ID.eq(PERSONAL_EXPENSE_CORRECTIONS.CATEGORY_ID))
                .where(PERSONAL_EXPENSE_CORRECTIONS.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID.eq(personId))
        )
        .collectList()
        .map(::mapPersonalExpense)
}

private fun mapPersonalExpense(r: List<Record>): PersonalExpense = PersonalExpense(
    mapPerson(r.first()),
    r.map(::mapPersonalExpenseCorrection)
)

private fun mapPersonalExpenseCorrection(r: Record): PersonalExpenseCorrection = PersonalExpenseCorrection(
    r.get(PERSONAL_EXPENSE_CORRECTIONS.ID),
    r.get(PERSONAL_EXPENSE_CORRECTIONS.AMOUNT).toBigDecimal(),
    r.get(PERSONAL_EXPENSE_CORRECTIONS.COMMENT),
    mapCategory(r)
)

private fun mapCategory(r: Record): Category = Category(
    r.get(CATEGORIES.ID),
    r.get(CATEGORIES.NAME),
    r.get(CATEGORIES.COMMENT)
)