package com.plcoding.translator_kmm.core.domain.utils

import kotlinx.coroutines.flow.MutableStateFlow

class IOSMutableStateFlow<T>(
    private val initialValue: T
): CommonMutableStateFlow<T>(MutableStateFlow(initialValue))
