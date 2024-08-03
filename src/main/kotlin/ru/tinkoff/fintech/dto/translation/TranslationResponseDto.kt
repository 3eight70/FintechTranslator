package ru.tinkoff.fintech.dto.translation

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "dto для ответа на запрос на перевод")
data class TranslationResponseDto (
    @Schema(description = "Ответ на запрос на перевод", example = "http 200 Привет мир")
    val translatedText: String?
)