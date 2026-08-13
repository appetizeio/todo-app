package io.appetize.todo

import java.time.LocalDate

data class Task(
    val id: String,
    val title: String,
    val done: Boolean = false,
    val dueDate: LocalDate? = null,
) {
    fun isOverdue(today: LocalDate) = !done && dueDate != null && dueDate < today

    fun isDueToday(today: LocalDate) = !done && dueDate == today
}
