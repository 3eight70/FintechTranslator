package ru.tinkoff.fintech.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.tinkoff.fintech.service.TranslationService

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Переводчик", description = "Позволяет переводить тексты")
class TranslationController(
    private val translationService: TranslationService
) {
    @PostMapping("/translate")
    @Operation(
        summary = "Перевод текста",
        description = "Позволяет перевести заданный текст"
    )
    fun translate(
        @RequestParam(name = "text") @Parameter(name = "Текст, подлежащий переводу") text: String,
        @RequestParam(name = "sourceLanguage") @Parameter(name = "Язык, на котором написан текст") sourceLang: String,
        @RequestParam(name = "targetLanguage") @Parameter(name = "Язык, на который нужно перевести текст") targetLang: String,
    ): String {
        return translationService.translate(text, sourceLang, targetLang)
    }
}