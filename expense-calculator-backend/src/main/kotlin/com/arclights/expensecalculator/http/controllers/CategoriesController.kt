package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Category
import com.arclights.expensecalculator.db.CategoryDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController("/category")
class CategoriesController(private val categoryDao: CategoryDao) {
    @PutMapping(path = ["/category"], consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCategory(@RequestBody category: Category): Mono<Category> = categoryDao.createUpdateCategory(category)

    @GetMapping(path = ["/category"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategories(): Flux<Category> = categoryDao.getCategories()

    @GetMapping(path = ["/category/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getCategory(@PathVariable id: UUID): Mono<Category> = categoryDao.getCategory(id)
}