package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.CalculationListing
import com.arclights.expensecalculator.service.Calculation
import com.arclights.expensecalculator.service.CalculationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController("/calculation")
@RequestMapping(path = ["/calculation"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CalculationController(private val calculationService: CalculationService) {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCalculation(@RequestBody calculation: Calculation) = calculationService.createUpdateCalculation(calculation)

    @GetMapping
    @ResponseBody
    fun getAvailableCalculations(): Flux<CalculationListing> = calculationService.getAvailableCalculations()

    @GetMapping(path = ["/{year}/{month}"])
    @ResponseBody
    fun getCalculation(@PathVariable year: Int, @PathVariable month: Int): Mono<Calculation> = calculationService.getCalculation(year, month)
}