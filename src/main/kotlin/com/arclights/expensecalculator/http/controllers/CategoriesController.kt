package com.arclights.expensecalculator.http.controllers

import com.arclights.expensecalculator.db.Category
import com.arclights.expensecalculator.db.CategoryDao
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController("/categories")
@RequestMapping(path = ["/categories"], produces = [MediaType.APPLICATION_JSON_VALUE])
class CategoriesController(private val categoryDao: CategoryDao) {
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun createUpdateCategory(@RequestBody category: Category): Mono<Category> =
        categoryDao.createUpdateCategory(category)

    @GetMapping
    @ResponseBody
    fun getCategories(): Flux<Category> = categoryDao.getCategories()

    @GetMapping(path = ["/{id}"])
    @ResponseBody
    fun getCategory(@PathVariable id: UUID): Mono<Category> = categoryDao.getCategory(id)
}