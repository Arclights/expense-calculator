package com.arclights.expensecalculator.service

import com.arclights.expensecalculator.db.CalculationDao
import com.arclights.expensecalculator.db.CalculationListing
import com.arclights.expensecalculator.db.PersonalExpense
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.arclights.expensecalculator.db.Income as DbIncome

@Service
class CalculationService(private val calculationDao: CalculationDao) {

    fun getCalculation(year: Int, month: Int): Mono<Calculation> = calculationDao.getCalculation(year, month)
        .map { it.toServiceModel() }

    fun getAvailableCalculations(): Flux<CalculationListing> = calculationDao.listAvailableCalculations()

    fun createUpdateCalculation(calculation: Calculation): Mono<Calculation> =
            Mono.just(calculation.toDbModel())
                .flatMap { calculationDao.createUpdateCalculation(it) }
                .map { it.toServiceModel() }

}

private fun Calculation.toDbModel() = com.arclights.expensecalculator.db.Calculation(
        this.id,
        this.year,
        this.month,
        this.personalCalculations.toDbIncomes(),
        this.expenses.toDbModel(),
        this.personalCalculations.toDbPersonalExpense()
)

private fun List<PersonCalculation>.toDbIncomes(): List<DbIncome> = this.map {
    DbIncome(
            it.income.amount,
            it.income.comment,
            it.person
    )
}

private fun List<PersonCalculation>.toDbPersonalExpense(): List<PersonalExpense> = this.map {
    PersonalExpense(
            it.person,
            it.expenseCorrections.map { it.toDbModel() }
    )
}

private fun PersonalExpenseCorrection.toDbModel(): com.arclights.expensecalculator.db.PersonalExpenseCorrection = com.arclights.expensecalculator.db.PersonalExpenseCorrection(
        this.id,
        this.amount,
        this.comment,
        this.category
)

private fun List<Expense>.toDbModel(): List<com.arclights.expensecalculator.db.Expense> = this.map {
    com.arclights.expensecalculator.db.Expense(
            it.amount,
            it.comment,
            it.card.toDbModel()
    )
}

private fun com.arclights.expensecalculator.db.Calculation.toServiceModel(): Calculation = Calculation(
        this.id,
        this.year,
        this.month,
        toPersonalCalculations(this.personalExpenseCorrections, this.incomes),
        this.expenses.toServiceModel()
)

private fun List<com.arclights.expensecalculator.db.Expense>.toServiceModel(): List<Expense> = this.map {
    Expense(
            it.amount,
            it.comment,
            it.card.toServiceModel()
    )
}

private fun com.arclights.expensecalculator.db.Card.toServiceModel(): Card = Card(
        this.id,
        this.name,
        this.comment
)

private fun Card.toDbModel(): com.arclights.expensecalculator.db.Card = com.arclights.expensecalculator.db.Card(
        this.id,
        this.name,
        this.comment
)

private fun toPersonalCalculations(
        personalExpenseCorrections: List<PersonalExpense>,
        incomes: List<com.arclights.expensecalculator.db.Income>
): List<PersonCalculation> {
    val expensesPerPerson = personalExpenseCorrections.groupBy { it.person }
    val incomesPerPerson = incomes.groupBy { it.person }

    return mapZip(incomesPerPerson, expensesPerPerson, emptyList(), emptyList()) {
        PersonCalculation(
                it.first,
                it.second.first().toServiceModel(),
                it.third.toServicePersonalExpenses()
        )
    }
}

private fun List<PersonalExpense>.toServicePersonalExpenses(): List<PersonalExpenseCorrection> = this.flatMap { personalExpense ->
    personalExpense.corrections.map {
        PersonalExpenseCorrection(
                it.id,
                it.amount,
                it.comment,
                it.category
        )
    }
}

private fun com.arclights.expensecalculator.db.Income.toServiceModel(): Income = Income(
        this.amount,
        this.comment,
        this.person
)

private fun <T, V1, V2, MV> mapZip(
        map1: Map<T, V1>,
        map2: Map<T, V2>,
        emptyValue1: V1,
        emptyValue2: V2,
        mappingFn: (Triple<T, V1, V2>) -> MV
): List<MV> = mapZip(map1, map2, emptyValue1, emptyValue2).map(mappingFn)

private fun <T, V1, V2> mapZip(
        map1: Map<T, V1>,
        map2: Map<T, V2>,
        emptyValue1: V1,
        emptyValue2: V2
): List<Triple<T, V1, V2>> {
    val keys1 = map1.keys
    val keys2 = map2.keys
    val commonKeys = keys1.intersect(keys2)
    val keysOnlyInMap1 = keys1.minus(commonKeys)
    val keysOnlyInMap2 = keys2.minus(commonKeys)

    return commonKeys.map { Triple(it, map1.getValue(it), map2.getValue(it)) }
        .plus(keysOnlyInMap1.map { Triple(it, map1.getValue(it), emptyValue2) })
        .plus(keysOnlyInMap2.map { Triple(it, emptyValue1, map2.getValue(it)) })
}
