package com.arclights.expensecalculator.http.controllers

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController("/category")
class CategoriesController {
    @PutMapping(path = ["/category"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCategory() {
        TODO()
    }

    @GetMapping(path = ["/category"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategories() {
        TODO()
    }

    @GetMapping(path = ["/category/:id"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategory() {
        TODO()
    }
}