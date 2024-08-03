package ru.tinkoff.fintech.service

import jakarta.servlet.http.HttpServletRequest
import ru.tinkoff.fintech.dto.TranslationRequestDto

/**
 * Сервис для работы с переводчиком
 */
interface TranslationService {
    /**
     * Запрос на перевод текста
     *
     * @param translationRequestDto - тело запроса
     * @param request HttpServletRequest, содержащий информацию о HTTP-запросе.
     *
     * @return String - строка, представляющая собой результат перевода
     */
    fun translate(
        translationRequestDto: TranslationRequestDto,
        request: HttpServletRequest
    ): String
}