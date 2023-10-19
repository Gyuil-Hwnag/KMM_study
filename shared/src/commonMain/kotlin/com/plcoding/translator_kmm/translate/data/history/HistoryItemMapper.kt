package com.plcoding.translator_kmm.translate.data.history

import com.plcoding.translator_kmm.translate.domain.histroy.HistoryItem
import database.HistoryEntity


fun HistoryEntity.toHistoryItem(): HistoryItem {
    return HistoryItem(
        id = id,
        fromLanguageCode = fromLanguageCode,
        fromText = fromText,
        toLanguageCode = toLanguageCode,
        toText = toText
    )
}
