package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CATEGORIES
import org.jooq.Record3
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class CategoryDao(private val dsl: DefaultDSLContext) {

    fun getCategories(): Flux<Category> = Flux
        .from(
                dsl.select(CATEGORIES.ID, CATEGORIES.NAME, CATEGORIES.COMMENT)
                    .from(CATEGORIES)
        )
        .map(this::mapCategory)

    fun getCategory(id: UUID): Mono<Category> = Mono
        .from(
                dsl.select(CATEGORIES.ID, CATEGORIES.NAME, CATEGORIES.COMMENT)
                    .from(CATEGORIES)
                    .where(CATEGORIES.ID.eq(id))
        )
        .map(this::mapCategory)

    fun createUpdateCategory(category: Category): Mono<Category> =
            if (category.id == null) {
                createCategory(category)
            } else {
                updateCategory(category)
            }

    private fun createCategory(category: Category): Mono<Category> = Mono
        .fromCallable {
            dsl.insertInto(CATEGORIES, CATEGORIES.NAME, CATEGORIES.COMMENT)
                .values(category.name, category.comment)
                .returning()
                .fetchOne()
        }
        .map { cr -> Category(cr.id, cr.name, cr.comment) }

    private fun updateCategory(category: Category): Mono<Category> = Mono
        .from(
                dsl.update(CATEGORIES)
                    .set(CATEGORIES.NAME, category.name)
                    .set(CATEGORIES.COMMENT, category.comment)
                    .where(CATEGORIES.ID.eq(category.id))
        )
        .thenReturn(category)

    private fun mapCategory(r: Record3<UUID, String, String>) = Category(r.get(CATEGORIES.ID), r.get(CATEGORIES.NAME), r.get(CATEGORIES.COMMENT))

}