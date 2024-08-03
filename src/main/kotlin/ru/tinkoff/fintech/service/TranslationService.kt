package ru.tinkoff.fintech.service

import jakarta.servlet.http.HttpServletRequest
import ru.tinkoff.fintech.dto.translation.TranslationRequestDto
import ru.tinkoff.fintech.dto.translation.TranslationResponseDto

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
     * @return TranslationResponseDto - dto, содержащее строку, представляющую собой результат перевода
     */
    fun translate(
        translationRequestDto: TranslationRequestDto,
        request: HttpServletRequest
    ): TranslationResponseDto
}