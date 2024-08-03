package ru.tinkoff.fintech.dao.repository

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.tinkoff.fintech.dao.entity.TranslationRequest

/**
 * Репозиторий для запросов на перевод текста
 */
@Repository
class TranslationRequestRepository(private val jdbcTemplate: JdbcTemplate) {
    /**
     * Сохранение записи в бд
     *
     * @param request - запрос на перевод, подлежащий сохранения в бд
     */
    @Transactional
    fun save(request: TranslationRequest){
        val sql = "INSERT INTO t_translation_requests (ip_address, original_text, translated_text) VALUES (?, ?, ?)"
        jdbcTemplate.update(sql, request.ipAddress, request.inputText, request.outputText)
    }
}