package com.plcoding.translator_kmm.translate.data.local

import com.plcoding.translator_kmm.core.domain.utils.CommonFlow
import com.plcoding.translator_kmm.core.domain.utils.toCommonFlow
import com.plcoding.translator_kmm.translate.domain.histroy.HistoryDataSource
import com.plcoding.translator_kmm.translate.domain.histroy.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow

class FakeHistoryDataSource: HistoryDataSource {

    private val _data = MutableStateFlow<List<HistoryItem>>(emptyList())

    override fun getHistory(): CommonFlow<List<HistoryItem>> {
        return _data.toCommonFlow()
    }

    override suspend fun insertHistoryItem(item: HistoryItem) {
        _data.value += item
    }
}
