package com.mk.kmpshowcase.presentation.screen.database

import androidx.compose.runtime.Immutable
import com.mk.kmpshowcase.domain.model.Note
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Immutable
data class NoteUiModel(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: String,
)

fun Note.toUiModel() = NoteUiModel(
    id = id,
    title = title,
    content = content,
    createdAt = formatTimestamp(createdAt),
)

private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.date} ${localDateTime.hour.toString().padStart(2, '0')}:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}
