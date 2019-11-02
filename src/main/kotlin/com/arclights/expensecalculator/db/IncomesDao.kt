package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.INCOMES
import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class IncomesDao(private val dsl: DefaultDSLContext) {

    fun createUpdateIncomes(calculationId: CalculationId, incomes: List<Income>): Flux<Pair<CalculationId, PersonId>> =
        deleteNotIncludedEntries(calculationId, incomes)
            .thenMany(Flux.fromIterable(incomes))
            .flatMap { createOrUpdateIncome(calculationId, it) }

    private fun deleteNotIncludedEntries(calculationId: CalculationId, incomes: List<Income>): Flux<PersonId> {
        val incomingPersonIds = incomes.mapNotNull { it.person.id }.toSet()
        return getPersonIdsForCalculationId(calculationId)
            .filter { incomingPersonIds.contains(it).not() }
            .flatMap { deleteIncome(calculationId, it) }
    }

    fun createOrUpdateIncome(calculationId: CalculationId, income: Income): Mono<Pair<CalculationId, PersonId>> =
        getIncome(calculationId, income.person)
            .flatMap { updateIncome(calculationId, income) }
            .switchIfEmpty(Mono.defer { createIncome(calculationId, income) })

    private fun createIncome(calculationId: CalculationId, income: Income): Mono<Pair<CalculationId, PersonId>> = Mono
        .fromCallable {
            dsl.insertInto(INCOMES, INCOMES.MONTHLY_CALCULATION_ID, INCOMES.PERSON_ID, INCOMES.AMOUNT, INCOMES.COMMENT)
                .values(calculationId, income.person.id, income.amount.toDouble(), income.comment)
                .returning(INCOMES.MONTHLY_CALCULATION_ID, INCOMES.PERSON_ID)
                .fetchOne()
        }
        .map { it.monthlyCalculationId to it.personId }

    private fun updateIncome(calculationId: CalculationId, income: Income): Mono<Pair<CalculationId, PersonId>> = Mono
        .fromCallable {
            dsl.update(INCOMES)
                .set(INCOMES.AMOUNT, income.amount.toDouble())
                .set(INCOMES.COMMENT, income.comment)
                .where(INCOMES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(INCOMES.PERSON_ID.eq(income.person.id))
                .returning(INCOMES.MONTHLY_CALCULATION_ID, INCOMES.PERSON_ID)
                .fetchOne()
        }
        .map { it.monthlyCalculationId to it.personId }

    fun getIncome(calculationId: CalculationId, person: Person): Mono<Income> = Mono
        .from(
            dsl.select(
                INCOMES.MONTHLY_CALCULATION_ID,
                INCOMES.PERSON_ID,
                INCOMES.AMOUNT,
                INCOMES.COMMENT,
                PERSONS.ID,
                PERSONS.NAME
            )
                .from(INCOMES)
                .leftJoin(PERSONS).on(PERSONS.ID.eq(INCOMES.PERSON_ID))
                .where(INCOMES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(INCOMES.PERSON_ID.eq(person.id))
        )
        .map { mapIncome(it) }

    private fun getPersonIdsForCalculationId(calculationId: CalculationId): Flux<PersonId> = Flux
        .from(
            dsl
                .select(INCOMES.PERSON_ID)
                .from(INCOMES)
                .where(INCOMES.MONTHLY_CALCULATION_ID.eq(calculationId))
        )
        .map { it.get(INCOMES.PERSON_ID) }

    private fun deleteIncome(calculationId: CalculationId, personId: PersonId): Mono<PersonId> = Mono
        .fromCallable {
            dsl
                .deleteFrom(INCOMES)
                .where(INCOMES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(INCOMES.PERSON_ID.eq(personId))
                .returning(INCOMES.PERSON_ID)
                .fetchOne()
        }
        .map { it.personId }
}

private fun mapIncome(r: Record): Income = Income(
    r.get(INCOMES.AMOUNT).toBigDecimal(),
    r.get(INCOMES.COMMENT),
    mapPerson(r)
)
