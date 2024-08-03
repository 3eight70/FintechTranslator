package ru.tinkoff.fintech.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.tinkoff.fintech.dto.TranslationRequestDto
import ru.tinkoff.fintech.service.TranslationService

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Переводчик", description = "Контроллер для обработки запросов на перевод текста")
class TranslationController(
    private val translationService: TranslationService
) {
    @PostMapping("/translate")
    @Operation(
        summary = "Перевод текста",
        description = "Позволяет перевести заданный текст"
    )
    fun translate(
        @ModelAttribute @Valid translationRequestDto: TranslationRequestDto,
        request: HttpServletRequest
    ): String {
        return translationService.translate(translationRequestDto, request)
    }
}