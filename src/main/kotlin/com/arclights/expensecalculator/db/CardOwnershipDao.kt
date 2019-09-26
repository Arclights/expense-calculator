package com.arclights.expensecalculator.db

import com.arclights.expensecalculator.db.Tables.CARDS
import com.arclights.expensecalculator.db.Tables.CARD_OWNERS
import com.arclights.expensecalculator.db.Tables.PERSONS
import org.jooq.Record
import org.jooq.impl.DefaultDSLContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
class CardOwnershipDao(private val dsl: DefaultDSLContext) {
    fun createCardOwnership(cardId: UUID, ownerId: UUID): Mono<Pair<UUID, UUID>> = Mono
        .from(
                dsl.insertInto(CARD_OWNERS, CARD_OWNERS.CARD_ID, CARD_OWNERS.OWNER_ID)
                    .values(cardId, ownerId)
        )
        .map { cardId to ownerId }

    fun deleteOwnershipsForCard(cardId: UUID): Mono<Int> = Mono
        .from(
                dsl.deleteFrom(CARD_OWNERS)
                    .where(CARD_OWNERS.CARD_ID.eq(cardId))
        )

    fun getCardOwnerships(): Flux<CardWithOwnership> = Flux
        .from(
                dsl.select()
                    .from(CARDS)
                    .leftJoin(CARD_OWNERS).on(CARD_OWNERS.CARD_ID.eq(CARDS.ID))
                    .leftJoin(PERSONS).on(CARD_OWNERS.OWNER_ID.eq(PERSONS.ID))
        )
        .groupBy { r -> r.get(CARDS.ID) }
        .map { it.collectList().map(this::mapOwnership) }
        .flatMap { it.flux() }

    fun getOwnershipForCard(cardId: UUID): Mono<CardWithOwnership> = Flux
        .from(
                dsl.select(CARDS.ID, CARDS.NAME, CARDS.COMMENT, PERSONS.ID, PERSONS.NAME)
                    .from(CARDS)
                    .leftJoin(CARD_OWNERS).on(CARD_OWNERS.CARD_ID.eq(CARDS.ID))
                    .leftJoin(PERSONS).on(CARD_OWNERS.OWNER_ID.eq(PERSONS.ID))
                    .where(CARDS.ID.eq(cardId))
        )
        .collectList()
        .map { mapOwnership(it) }

    private fun mapOwnership(r: List<Record>): CardWithOwnership = CardWithOwnership(
            mapCard(r.first()),
            r.filter { it.get(PERSONS.ID) != null }.map { mapPerson(it) }
    )
}