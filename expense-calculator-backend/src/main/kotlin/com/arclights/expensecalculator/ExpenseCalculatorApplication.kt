package com.arclights.expensecalculator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExpenseCalculatorApplication

fun main(args: Array<String>) {
    runApplication<ExpenseCalculatorApplication>(*args)
}