package ru.tinkoff.fintech.service

interface TranslationService {
    fun translate(text: String,
                  sourceLanguage: String,
                  targetLanguage: String): String
}