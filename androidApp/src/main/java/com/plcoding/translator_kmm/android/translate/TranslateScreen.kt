@file:OptIn(ExperimentalComposeUiApi::class)

package com.plcoding.translator_kmm.android.translate

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.plcoding.translator_kmm.android.R
import com.plcoding.translator_kmm.android.translate.component.LanguageDropDown
import com.plcoding.translator_kmm.android.translate.component.SwapLanguagesButton
import com.plcoding.translator_kmm.android.translate.component.TranslateHistoryItem
import com.plcoding.translator_kmm.android.translate.component.TranslateTextField
import com.plcoding.translator_kmm.android.translate.component.rememberTextToSpeech
import com.plcoding.translator_kmm.translate.domain.translate.TranslateError
import com.plcoding.translator_kmm.translate.presentation.TranslateEvent
import com.plcoding.translator_kmm.translate.presentation.TranslateState
import java.util.Locale

@Composable
fun TranslateScreen(
    state: TranslateState,
    onEvent: (TranslateEvent) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    var performScrollToTop by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state.error) {
        val message = when (state.error) {
            TranslateError.SERVICE_UNAVAILABLE -> context.getString(R.string.error_service_unavailable)
            TranslateError.CLIENT_ERROR -> context.getString(R.string.client_error)
            TranslateError.SERVER_ERROR -> context.getString(R.string.server_error)
            TranslateError.UNKNOWN_ERROR -> context.getString(R.string.unknown_error)
            else -> null
        }
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            onEvent(TranslateEvent.OnErrorSeen)
        }
    }

    LaunchedEffect(key1 = performScrollToTop) {
        if (performScrollToTop) {
            scrollState.animateScrollToItem(0)
            performScrollToTop = false
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.size(48.dp),
                onClick = { onEvent(TranslateEvent.RecordAudio) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Image(
                    modifier = Modifier.padding(12.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.mic),
                    contentDescription = stringResource(id = R.string.record_audio),
                    contentScale = ContentScale.FillBounds
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = scrollState
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LanguageDropDown(
                        language = state.fromLanguage,
                        isOpen = state.isChoosingFromLanguage,
                        onClick = { onEvent(TranslateEvent.OpenFromLanguageDropDown) },
                        onDismiss = { onEvent(TranslateEvent.StopChoosingLanguage) },
                        onSelectLanguage = { onEvent(TranslateEvent.ChooseFromLanguage(it)) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SwapLanguagesButton(
                        onClick = { onEvent(TranslateEvent.SwapLanguages) }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    LanguageDropDown(
                        language = state.toLanguage,
                        isOpen = state.isChoosingToLanguage,
                        onClick = { onEvent(TranslateEvent.OpenToLanguageDropDown) },
                        onDismiss = { onEvent(TranslateEvent.StopChoosingLanguage) },
                        onSelectLanguage = { onEvent(TranslateEvent.ChooseToLanguage(it)) }
                    )
                }
            }
            item {
                val clipboardManager = LocalClipboardManager.current
                val keyboardController = LocalSoftwareKeyboardController.current
                val textToSpeech = rememberTextToSpeech()

                TranslateTextField(
                    modifier = Modifier.fillMaxWidth(),
                    fromText = state.fromText,
                    toText = state.toText,
                    isTranslating = state.isTranslating,
                    fromLanguage = state.fromLanguage,
                    toLanguage = state.toLanguage,
                    onTranslateClick = {
                        keyboardController?.hide()
                        onEvent(TranslateEvent.Translate)
                    },
                    onTextChange = { onEvent(TranslateEvent.ChangeTranslationText(it)) },
                    onCopyClick = { text ->
                        clipboardManager.setText(
                            buildAnnotatedString { append(text) }
                        )
                        Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG).show()
                    },
                    onCloseClick = { onEvent(TranslateEvent.CloseTranslation) },
                    onWriteTextSpeakerClick = {
                        textToSpeech.language = state.fromLanguage.toLocale() ?: Locale.ENGLISH
                        textToSpeech.speak(
                            state.fromText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    },
                    onTranslateTextSpeakerClick = {
                        textToSpeech.language = state.toLanguage.toLocale() ?: Locale.ENGLISH
                        textToSpeech.speak(
                            state.toText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                    },
                    onTextFieldClick = { onEvent(TranslateEvent.EditTranslation) }
                )
            }
            item {
                if (state.history.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.history),
                        style = MaterialTheme.typography.h2
                    )
                }
            }

            items(
                items = state.history
            ) { item ->
                TranslateHistoryItem(
                    modifier = Modifier.fillMaxWidth(),
                    item = item,
                    onClick = {
                        onEvent(TranslateEvent.SelectHistoryItem(item))
                        performScrollToTop = true
                    }
                )
            }

            item { Spacer(modifier = Modifier.fillMaxWidth().height(12.dp)) }
        }
    }
}
