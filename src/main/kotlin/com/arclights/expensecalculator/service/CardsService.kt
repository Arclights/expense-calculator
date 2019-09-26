package com.arclights.expensecalculator.service

import com.arclights.expensecalculator.db.CardDao
import com.arclights.expensecalculator.db.CardOwnershipDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class CardsService(private val cardDao: CardDao, private val cardOwnershipDao: CardOwnershipDao) {

    @Transactional
    fun createUpdateCard(cardWithOwnership: CardWithOwnership): Mono<CardWithOwnership> = cardDao.createUpdateCard(cardWithOwnership.toDbCard())
        .map { it.id }
        .flatMap { id -> Mono.justOrEmpty(id) }
        .flatMap { cardId ->
            cardOwnershipDao
                .deleteOwnershipsForCard(cardId)
                .flatMapMany { Flux.fromIterable(cardWithOwnership.owners) }
                .flatMap { it.id?.let { ownerId -> cardOwnershipDao.createCardOwnership(cardId, ownerId) } }
                .collectList()
                .flatMap { getCard(cardId) }
        }

    fun getCards(): Flux<CardWithOwnership> = cardOwnershipDao.getCardOwnerships()
        .map { CardWithOwnership.fromDbModel(it) }

    fun getCard(id: UUID): Mono<CardWithOwnership> = cardOwnershipDao.getOwnershipForCard(id)
        .map { CardWithOwnership.fromDbModel(it) }
}