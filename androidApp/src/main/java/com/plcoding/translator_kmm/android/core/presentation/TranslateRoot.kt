package com.plcoding.translator_kmm.android.core.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.translator_kmm.android.translate.AndroidTranslateViewModel
import com.plcoding.translator_kmm.android.translate.TranslateScreen
import com.plcoding.translator_kmm.android.voice_to_text.data.presentation.AndroidVoiceToTextViewModel
import com.plcoding.translator_kmm.android.voice_to_text.data.presentation.VoiceToTextScreen
import com.plcoding.translator_kmm.translate.presentation.TranslateEvent
import com.plcoding.translator_kmm.voice_to_text.presentation.VoiceToTextEvent

const val VOICE_RESULT = "voiceResult"

@Composable
fun TranslateRoot() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.TRANSLATE
    ) {
        composable(route = Routes.TRANSLATE) {
            val viewModel = hiltViewModel<AndroidTranslateViewModel>()
            val state by viewModel.state.collectAsState()
            val voiceResult by it.savedStateHandle
                .getStateFlow<String?>(VOICE_RESULT, null)
                .collectAsState()

            LaunchedEffect(key1 = voiceResult) {
                viewModel.onEvent(TranslateEvent.SubmitVoiceResult(voiceResult))
                it.savedStateHandle[VOICE_RESULT] = null
            }

            TranslateScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is TranslateEvent.RecordAudio -> navController.navigate(Routes.VOICE_TO_TEXT + "/${state.fromLanguage.language.langCode}")
                        else -> viewModel.onEvent(event)
                    }
                }
            )
        }
        composable(
            route = Routes.VOICE_TO_TEXT + "/{language}",
            arguments = listOf(
                navArgument("languageCode") {
                    type = NavType.StringType
                    defaultValue = "en"
                }
            )
        ) { backStackEntry ->
            val languageCode = backStackEntry.arguments?.getString("languageCode") ?: "en"
            val viewModel = hiltViewModel<AndroidVoiceToTextViewModel>()
            val state by viewModel.state.collectAsState()

            VoiceToTextScreen(
                state = state,
                languageCode = languageCode,
                onResult = { spokenText ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(VOICE_RESULT, spokenText)
                    navController.popBackStack()
                },
                onEvent = { event ->
                    when(event) {
                        is VoiceToTextEvent.Close -> navController.popBackStack()
                        else -> viewModel.onEvent(event)
                    }
                }
            )

        }
    }
}
