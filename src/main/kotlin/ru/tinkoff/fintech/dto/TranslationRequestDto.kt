package ru.tinkoff.fintech.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull

@Schema(description = "dto для запроса на перевод текста")
data class TranslationRequestDto (
    @Schema(description = "Текст, подлежащий переводу", example = "Привет мир")
    @NotNull
    @Max(value = 1000)
    val q: String,
    @Schema(description = "Язык, на котором написан текст", example = "ru")
    @NotNull
    @Max(value = 100)
    val source: String,
    @Schema(description = "Язык, на который нужно перевести текст", example = "en")
    @NotNull
    @Max(value = 100)
    val target: String
)