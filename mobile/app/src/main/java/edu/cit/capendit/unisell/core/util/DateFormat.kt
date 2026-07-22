package edu.cit.capendit.unisell.core.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")

/**
 * Backend sends LocalDateTime as an ISO string with no timezone (e.g. "2026-07-22T10:15:30").
 * Falls back to the raw string if parsing fails, so a format change upstream never crashes the list.
 */
fun formatTimestamp(raw: String?): String {
    if (raw.isNullOrBlank()) return "—"
    return try {
        LocalDateTime.parse(raw).format(DISPLAY_FORMATTER)
    } catch (e: DateTimeParseException) {
        raw
    }
}
