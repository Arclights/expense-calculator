package com.arclights.expensecalculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class ExpenseCalculatorApplication

fun main(args: Array<String>) {
    runApplication<ExpenseCalculatorApplication>(*args)
}