package com.arclights.expensecalculator.http.controllers

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController("/calculation")
class CalculationController {
    @PutMapping(path = ["/calculation"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCalculation() {
        TODO()
    }

    @GetMapping(path = ["/calculation"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCalculation() {
        TODO()
    }
}