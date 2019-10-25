package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.*
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

private val INCOME_PERSONS = PERSONS.`as`("IncomePerson")
private val EXPENSE_PERSONS = PERSONS.`as`("ExpensePerson")

@Repository
class CalculationDao(
        private val dsl: DefaultDSLContext,
        private val incomesDao: IncomesDao,
        private val expensesDao: ExpensesDao,
        private val personalExpensesDao: PersonalExpensesDao
) {

    fun listAvailableCalculations(): Flux<CalculationListing> = Flux
        .from(
                dsl.select(MONTHLY_CALCULATIONS.YEAR, MONTHLY_CALCULATIONS.MONTH)
                    .from(MONTHLY_CALCULATIONS)
        )
        .map { r -> CalculationListing(r.get(MONTHLY_CALCULATIONS.YEAR), r.get(MONTHLY_CALCULATIONS.MONTH)) }

    fun getCalculation(year: Int, month: Int): Mono<Calculation> = Flux
        .from(
                dsl.select(
                        MONTHLY_CALCULATIONS.ID,
                        MONTHLY_CALCULATIONS.YEAR,
                        MONTHLY_CALCULATIONS.MONTH,
                        INCOMES.AMOUNT,
                        INCOMES.COMMENT,
                        INCOMES.PERSON_ID,
                        EXPENSES.AMOUNT,
                        EXPENSES.COMMENT,
                        EXPENSES.CARD_ID,
                        PERSONAL_EXPENSE_CORRECTIONS.ID,
                        PERSONAL_EXPENSE_CORRECTIONS.AMOUNT,
                        PERSONAL_EXPENSE_CORRECTIONS.COMMENT,
                        PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID,
                        CARDS.ID,
                        CARDS.NAME,
                        CARDS.COMMENT,
                        INCOME_PERSONS.ID,
                        INCOME_PERSONS.NAME,
                        EXPENSE_PERSONS.ID,
                        EXPENSE_PERSONS.NAME,
                        CATEGORIES.ID,
                        CATEGORIES.NAME,
                        CATEGORIES.COMMENT
                )
                    .from(MONTHLY_CALCULATIONS)
                    .leftJoin(INCOMES).on(INCOMES.MONTHLY_CALCULATION_ID.eq(MONTHLY_CALCULATIONS.ID))
                    .leftJoin(EXPENSES).on(EXPENSES.MONTHLY_CALCULATION_ID.eq(MONTHLY_CALCULATIONS.ID))
                    .leftJoin(PERSONAL_EXPENSE_CORRECTIONS).on(PERSONAL_EXPENSE_CORRECTIONS.MONTHLY_CALCULATION_ID.eq(MONTHLY_CALCULATIONS.ID))
                    .leftJoin(CATEGORIES).on(PERSONAL_EXPENSE_CORRECTIONS.CATEGORY_ID.eq(CATEGORIES.ID))
                    .leftJoin(CARDS).on(CARDS.ID.eq(EXPENSES.CARD_ID))
                    .leftJoin(INCOME_PERSONS).on(INCOME_PERSONS.ID.eq(INCOMES.PERSON_ID))
                    .leftJoin(EXPENSE_PERSONS).on(EXPENSE_PERSONS.ID.eq(PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID))
                    .where(MONTHLY_CALCULATIONS.YEAR.eq(year).and(MONTHLY_CALCULATIONS.MONTH.eq(month)))
        )
        .collectList()
        .flatMap { r -> if (r.isEmpty()) Mono.empty() else Mono.just(r) }
        .map { mapCalculation(it) }

    fun createUpdateCalculation(c: Calculation): Mono<Calculation> =
            if (c.id == null) {
                createCalculation(c)
            } else {
                updateCalculation(c)
            }
                .flatMap { calculationId ->
                    Flux
                        .merge(
                                listOf(
                                        incomesDao.createUpdateIncomes(calculationId, c.incomes),
                                        expensesDao.createUpdateExpenses(calculationId, c.expenses),
                                        personalExpensesDao.createUpdatePersonalExpenses(calculationId, c.personalExpenseCorrections)
                                )
                        )
                        .collectList()
                }
                .compose { getCalculation(c.year, c.month) }

    private fun createCalculation(c: Calculation): Mono<UUID> = Mono
        .fromCallable {
            dsl.insertInto(MONTHLY_CALCULATIONS, MONTHLY_CALCULATIONS.YEAR, MONTHLY_CALCULATIONS.MONTH)
                .values(c.year, c.month)
                .returning()
                .fetchOne()
        }
        .map { it.id }

    private fun updateCalculation(c: Calculation): Mono<UUID> = Mono.fromCallable {
        dsl.update(MONTHLY_CALCULATIONS)
            .set(MONTHLY_CALCULATIONS.YEAR, c.year)
            .set(MONTHLY_CALCULATIONS.MONTH, c.month)
            .where(MONTHLY_CALCULATIONS.ID.eq(c.id!!))
            .returning()
            .fetchOne()
    }
        .map { it.id }
}

private fun mapCalculation(r: List<Record>) = with(r) {
    Calculation(
            first().get(MONTHLY_CALCULATIONS.ID),
            first().get(MONTHLY_CALCULATIONS.YEAR),
            first().get(MONTHLY_CALCULATIONS.MONTH),
            mapIncomes(r),
            mapExpenses(r),
            mapPersonalExpenses(r)
    )
}

private fun mapIncomes(r: List<Record>): List<Income> = r.distinctBy { it.get(INCOMES.PERSON_ID) }
    .filter { it.get(INCOMES.AMOUNT) != null }
    .map {
        Income(
                it.get(INCOMES.AMOUNT).toBigDecimal(),
                it.get(INCOMES.COMMENT),
                mapIncomePerson(it)
        )
    }

private fun mapIncomePerson(r: Record): Person = Person(r.get(INCOME_PERSONS.ID), r.get(INCOME_PERSONS.NAME))

private fun mapExpenses(r: List<Record>): List<Expense> = r.distinctBy { it.get(EXPENSES.CARD_ID) }
    .filter{it.get(EXPENSES.AMOUNT) != null}
    .map {
        Expense(
                it.get(EXPENSES.AMOUNT).toBigDecimal(),
                it.get(EXPENSES.COMMENT),
                mapCard(it)
        )
    }

private fun mapPersonalExpenses(r: List<Record>): List<PersonalExpense> = r.distinctBy { it.get(PERSONAL_EXPENSE_CORRECTIONS.ID) }
    .filter{it.get(EXPENSE_PERSONS.ID) != null && it.get(PERSONAL_EXPENSE_CORRECTIONS.ID) != null}
    .groupBy { it.get(PERSONAL_EXPENSE_CORRECTIONS.PERSON_ID) }
    .map { e ->
        PersonalExpense(
                e.value.first().let { mapExpensePerson(it) },
                e.value.map { mapPersonalExpenseCorrections(it) }
        )
    }

private fun mapExpensePerson(r: Record): Person = Person(r.get(EXPENSE_PERSONS.ID), r.get(EXPENSE_PERSONS.NAME))

private fun mapPersonalExpenseCorrections(r: Record) = PersonalExpenseCorrection(
        r.get(PERSONAL_EXPENSE_CORRECTIONS.ID),
        r.get(PERSONAL_EXPENSE_CORRECTIONS.AMOUNT).toBigDecimal(),
        r.get(PERSONAL_EXPENSE_CORRECTIONS.COMMENT),
        mapCategory(r)
)

private fun mapCategory(r: Record): Category = Category(r.get(CATEGORIES.ID), r.get(CATEGORIES.NAME), r.get(CATEGORIES.COMMENT))