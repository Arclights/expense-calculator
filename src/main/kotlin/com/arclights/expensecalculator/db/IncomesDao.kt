package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.INCOMES
import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class IncomesDao(private val dsl: DefaultDSLContext) {

    fun createUpdateIncomes(calculationId: UUID, incomes: List<Income>): Flux<Income> =
            Flux.mergeSequential(incomes.map { createOrUpdateIncome(calculationId, it) })

    fun createOrUpdateIncome(calculationId: UUID, income: Income): Mono<Income> =
            getIncome(calculationId, income.person)
                .flatMap { updateIncome(calculationId, income) }
                .switchIfEmpty(Mono.defer { createIncome(calculationId, income) })

    private fun createIncome(calculationId: UUID, income: Income): Mono<Income> = Mono
        .fromCallable {
            dsl.insertInto(INCOMES, INCOMES.MONTHLY_CALCULATION_ID, INCOMES.PERSON_ID, INCOMES.AMOUNT, INCOMES.COMMENT)
                .values(calculationId, income.person.id, income.amount.toDouble(), income.comment)
                .returning()
                .fetchOne()
        }
        .map { mapIncome(it) }

    private fun updateIncome(calculationId: UUID, income: Income): Mono<Income> = Mono
        .fromCallable {
            dsl.update(INCOMES)
                .set(INCOMES.AMOUNT, income.amount.toDouble())
                .set(INCOMES.COMMENT, income.comment)
                .where(INCOMES.MONTHLY_CALCULATION_ID.eq(calculationId))
                .and(INCOMES.PERSON_ID.eq(income.person.id))
                .returning()
                .fetchOne()
        }
        .map { mapIncome(it) }

    fun getIncome(calculationId: UUID, person: Person): Mono<Income> = Mono
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
}

private fun mapIncome(r: Record): Income = Income(
        r.get(INCOMES.AMOUNT).toBigDecimal(),
        r.get(INCOMES.COMMENT),
        mapPerson(r)
)
