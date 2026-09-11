package com.vivek.bhaiassistant

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

class SimpleRecognitionListener(
    private val onResult: (String) -> Unit,
    private val onError: (Int) -> Unit
) : RecognitionListener {

    override fun onResults(results: Bundle?) {

        val resultsList =
            results?.getStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION
            )

        val text =
            resultsList?.firstOrNull() ?: ""

        onResult(text)
    }

    override fun onError(error: Int) {
        onError(error)
    }

    override fun onReadyForSpeech(
        params: Bundle?
    ) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(
        rmsdB: Float
    ) {}

    override fun onBufferReceived(
        buffer: ByteArray?
    ) {}

    override fun onEndOfSpeech() {}

    override fun onPartialResults(
        partialResults: Bundle?
    ) {}

    override fun onEvent(
        eventType: Int,
        params: Bundle?
    ) {}
}
