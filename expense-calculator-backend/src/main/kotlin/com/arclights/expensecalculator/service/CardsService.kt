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
    fun createUpdateCard(card: Card): Mono<Card> = cardDao.createUpdateCard(card.toDbCard())
        .map { it.id }
        .flatMap { id -> Mono.justOrEmpty(id) }
        .flatMap { cardId ->
            cardOwnershipDao
                .deleteOwnershipsForCard(cardId)
                .flatMapMany { Flux.fromIterable(card.owners) }
                .flatMap { it.id?.let { ownerId -> cardOwnershipDao.createCardOwnership(cardId, ownerId) } }
                .collectList()
                .flatMap { getCard(cardId) }
        }

    fun getCards(): Flux<Card> = cardOwnershipDao.getCardOwnerships()
        .map { Card.fromDbModel(it) }

    fun getCard(id: UUID): Mono<Card> = cardOwnershipDao.getOwnershipForCard(id)
        .map { Card.fromDbModel(it) }
}