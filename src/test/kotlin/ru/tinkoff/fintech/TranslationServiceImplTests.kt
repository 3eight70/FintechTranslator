package ru.tinkoff.fintech

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.tinkoff.fintech.dao.entity.TranslationRequest
import ru.tinkoff.fintech.dao.repository.TranslationRequestRepository
import ru.tinkoff.fintech.dto.translation.TranslationRequestDto
import ru.tinkoff.fintech.service.Impl.TranslationServiceImpl
import java.net.SocketException
import kotlin.test.Test
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class TranslationServiceImplTests {
    @Mock
    private lateinit var restTemplate: RestTemplate

    @Mock
    private lateinit var translationRequestRepository: TranslationRequestRepository

    @Mock
    private lateinit var request: HttpServletRequest

    private lateinit var translationServiceImpl: TranslationServiceImpl

    private val apiUrl = "http://mock:5000/translate"

    @BeforeEach
    fun setUp() {
        translationServiceImpl = TranslationServiceImpl(
            restTemplate,
            apiUrl,
            translationRequestRepository
        )
    }

    @Test
    fun `translate should return translated text`() {
        val translationRequestDto = TranslationRequestDto(
            q = "Hello world",
            source = "en",
            target = "ru"
        )

        val responseBody: Map<String, String> = mapOf("translatedText" to "Привет мир")
        val response: ResponseEntity<Map<String, String>> = ResponseEntity(responseBody, HttpStatus.OK)

        whenever(restTemplate.exchange(
            eq(apiUrl),
            eq(HttpMethod.POST),
            any(),
            eq(Map::class.java)
        )).thenReturn(response) as Map<String, String>
        whenever(request.remoteAddr).thenReturn("127.0.0.1")

        val result = translationServiceImpl.translate(translationRequestDto, request)

        assertEquals("Привет мир", result.translatedText)
        verify(translationRequestRepository).save(any<TranslationRequest>())
    }


    @Test
    fun `translate should handle SocketException`() {
        val translationRequestDto = TranslationRequestDto(
            q = "Hello world",
            source = "en",
            target = "ru"
        )

        whenever(restTemplate.exchange(any<String>(), eq(HttpMethod.POST), any(), eq(Map::class.java)))
            .thenThrow(SocketException("Service Unavailable"))
        whenever(request.remoteAddr).thenReturn("127.0.0.1")

        val result = translationServiceImpl.translate(translationRequestDto, request)

        result.translatedText?.let { assertTrue(it.contains("http 503 Удаленный сервис временно недоступен")) }

        verify(translationRequestRepository).save(any<TranslationRequest>())
    }

    @Test
    fun `translate should handle HttpClientErrorException`() {
        val translationRequestDto = TranslationRequestDto(
            q = "Hello world",
            source = "en",
            target = "ru"
        )

        val errorResponse = ResponseEntity("{\"error\": \"Bad Request\"}", HttpStatus.BAD_REQUEST)

        whenever(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.POST), any(), eq(Map::class.java)))
            .thenThrow(
                HttpClientErrorException(
                    HttpStatus.BAD_REQUEST,
                    "Bad Request",
                    errorResponse.body?.toByteArray(),
                    null
                )
            )

        whenever(request.remoteAddr).thenReturn("127.0.0.1")

        val result = translationServiceImpl.translate(translationRequestDto, request)

        assertTrue(result.translatedText?.contains("http 400 Bad Request") ?: false)

        verify(translationRequestRepository).save(any<TranslationRequest>())
    }
}