package com.plcoding.translator_kmm.core.domain.utils

import kotlinx.coroutines.flow.MutableStateFlow

expect class CommonMutableStateFlow<T>(flow: MutableStateFlow<T>): MutableStateFlow<T>

fun <T> MutableStateFlow<T>.toCommonFlow() = CommonMutableStateFlow(this)
