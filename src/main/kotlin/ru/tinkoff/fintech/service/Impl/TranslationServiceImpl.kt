package ru.tinkoff.fintech.service.Impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.dao.entity.TranslationRequest
import ru.tinkoff.fintech.dao.repository.TranslationRequestRepository
import ru.tinkoff.fintech.dto.translation.TranslationRequestDto
import ru.tinkoff.fintech.dto.translation.TranslationResponseDto
import ru.tinkoff.fintech.service.TranslationService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

@Service
class TranslationServiceImpl(
    private val restTemplate: RestTemplate,
    @Value(value = "\${translator.url}")
    private val apiUrl: String,
    private val translationRequestRepository: TranslationRequestRepository
) : TranslationService {
    private val executor = Executors.newFixedThreadPool(10)
    private val objectMapper = jacksonObjectMapper()

    override fun translate(
        translationRequestDto: TranslationRequestDto,
        request: HttpServletRequest
    ): TranslationResponseDto {
        val words = translationRequestDto.q.split(" ")
        val headers = HttpHeaders()
        val errorAtomic = AtomicReference<String?>(null)
        headers.contentType = MediaType.APPLICATION_JSON

        val futures = words.map { word ->
            val httpRequest = TranslationRequestDto(
                word,
                translationRequestDto.source,
                translationRequestDto.target
            )

            val entity = HttpEntity(httpRequest, headers)
            executor.submit<String> {
                if (errorAtomic.get() != null) return@submit "Ошибка обработки"

                try {
                    val response = restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        entity,
                        Map::class.java
                    )

                    response.body?.get("translatedText") as String?
                } catch (ex: HttpClientErrorException) {
                    val errorMessage = ex.responseBodyAsString
                    val errorDetails = try {
                        val errorMap = objectMapper.readValue<Map<String, String>>(errorMessage)
                        errorMap["error"] ?: "Что-то пошло не так"
                    } catch (e: Exception) {
                        "Ошибка во время парсинга json'а: ${e.message}"
                    }
                    errorAtomic.set("http ${ex.statusCode.value()} $errorDetails")
                    "Ошибка обработки"
                } catch (ex: Exception) {
                    errorAtomic.set("http 500 ${ex.message}")
                    "Ошибка обработки"
                }
            }
        }

        val translatedText = futures.joinToString(" ") { it.get() }
        val result = if (errorAtomic.get() != null)
            errorAtomic.get()!! else "http 200 $translatedText"

        val translationRequest = TranslationRequest(
            ipAddress = request.remoteAddr,
            inputText = translationRequestDto.q,
            outputText = result
        )

        translationRequestRepository.save(translationRequest)

        return TranslationResponseDto(result)
    }
}