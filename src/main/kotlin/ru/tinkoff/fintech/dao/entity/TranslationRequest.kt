package ru.tinkoff.fintech.dao.entity

import java.time.LocalDateTime
import java.util.*

/**
 * Запрос на перевод текста
 */
data class TranslationRequest (
    /**
     * Идентификатор
     */
    val id: UUID = UUID.randomUUID(),

    /**
     * Время отправки запроса на перевод
     */
    val requestTime: LocalDateTime = LocalDateTime.now(),

    /**
     * IP адрес, с которого был послан запрос
     */
    val ipAddress: String,

    /**
     * Входная строка, подлежащая переводу
     * Пример: Hello world
     */
    val inputText: String,

    /**
     * Результат перевода + статус ответа на запрос
     * Пример: http 200 Привет мир
     */
    val outputText: String
)