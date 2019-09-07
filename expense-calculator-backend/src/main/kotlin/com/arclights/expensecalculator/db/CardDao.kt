package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CARDS
import org.jooq.Record3
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class CardDao(private val dsl: DefaultDSLContext) {

    fun getCards(): Flux<Card> = Flux
        .from(
                dsl.select(CARDS.ID, CARDS.NAME, CARDS.COMMENT)
                    .from(CARDS)
        )
        .map(this::mapCard)

    fun getCard(id: UUID): Mono<Card> = Mono
        .from(
                dsl.select(CARDS.ID, CARDS.NAME, CARDS.COMMENT)
                    .from(CARDS)
                    .where(CARDS.ID.eq(id))
        )
        .map(this::mapCard)

    private fun mapCard(r: Record3<UUID, String, String>) = Card(r.get(CARDS.ID), r.get(CARDS.NAME), r.get(CARDS.COMMENT))
}