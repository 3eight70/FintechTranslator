package ru.tinkoff.fintech.service.Impl

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import ru.tinkoff.fintech.dto.TranslationRequestDto
import ru.tinkoff.fintech.service.TranslationService
import java.net.URI
import java.util.concurrent.Executors

@Service
class TranslationServiceImpl(
    private val restTemplate: RestTemplate,
    @Value(value = "\${translator.url}")
    private val apiUrl: String
) : TranslationService {
    private val executor = Executors.newFixedThreadPool(10)

    override fun translate(
        translationRequestDto: TranslationRequestDto,
        request: HttpServletRequest
    ): String {
        val words = translationRequestDto.q.split(" ")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val futures = words.map { word ->
            val httpRequest = TranslationRequestDto(
                word,
                translationRequestDto.source,
                translationRequestDto.target
            )

            val entity = HttpEntity(httpRequest, headers)
            executor.submit<String> {
                val response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String::class.java
                )

                response.body ?: ""
            }
        }

        return futures.joinToString(" ") { it.get() }
    }
}