package com.plcoding.translator_kmm.voice_to_text.domain

import com.plcoding.translator_kmm.core.domain.utils.CommonStateFlow

interface VoiceToTextParser {
    val state: CommonStateFlow<VoiceToTextParserState>
    fun startListening(languageCode: String)
    fun stopListening()
    fun cancel()
    fun reset()
}
