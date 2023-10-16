package com.plcoding.translator_kmm.translate.domain.histroy

import com.plcoding.translator_kmm.core.domain.utils.CommonFlow
import kotlinx.coroutines.flow.Flow

interface HistoryDataSource {
    fun getHistory(): CommonFlow<List<HistoryItem>>
    suspend fun insertHistoryItem(item: HistoryItem)
}
