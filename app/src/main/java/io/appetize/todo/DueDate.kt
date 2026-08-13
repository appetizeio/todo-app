package io.appetize.todo

import java.time.LocalDate
import java.time.format.DateTimeFormatter

// American style, matching the rest of the Appetize web app
private val formatter = DateTimeFormatter.ofPattern("MMM d")

fun formatDueDate(date: LocalDate, today: LocalDate = LocalDate.now()): String =
    when (date) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        today.minusDays(1) -> "Yesterday"
        else -> formatter.format(date)
    }
