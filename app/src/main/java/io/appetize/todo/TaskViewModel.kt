package io.appetize.todo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

enum class TaskFilter(val label: String) {
    All("All"),
    Active("Active"),
    Done("Done"),
}

class TaskViewModel(private val store: TaskStore) : ViewModel() {
    private val _tasks = MutableStateFlow(store.load())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _filter = MutableStateFlow(TaskFilter.All)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    private val _highlightedId = MutableStateFlow<String?>(null)
    val highlightedId: StateFlow<String?> = _highlightedId.asStateFlow()

    /** Undated tasks sort last; otherwise soonest first, done tasks below. */
    fun visible(): List<Task> {
        val filtered = when (_filter.value) {
            TaskFilter.All -> _tasks.value
            TaskFilter.Active -> _tasks.value.filterNot { it.done }
            TaskFilter.Done -> _tasks.value.filter { it.done }
        }
        return filtered.sortedWith(
            compareBy({ it.done }, { it.dueDate ?: LocalDate.MAX })
        )
    }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun add(title: String, dueDate: LocalDate? = null) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) {
            return
        }
        update(
            _tasks.value + Task(
                id = UUID.randomUUID().toString(),
                title = trimmed,
                dueDate = dueDate,
            )
        )
    }

    fun toggle(id: String) {
        update(
            _tasks.value.map { task ->
                if (task.id == id) task.copy(done = !task.done) else task
            }
        )
    }

    fun remove(id: String) {
        update(_tasks.value.filterNot { it.id == id })
    }

    fun clearCompleted() {
        update(_tasks.value.filterNot { it.done })
    }

    fun apply(link: DeepLink) {
        when (link) {
            is DeepLink.OpenTask -> {
                _filter.value = TaskFilter.All
                _highlightedId.value = link.id
            }

            is DeepLink.NewTask -> link.title?.let { add(it, link.dueDate) }
            DeepLink.List -> _highlightedId.value = null
        }
    }

    fun clearHighlight() {
        _highlightedId.value = null
    }

    private fun update(tasks: List<Task>) {
        _tasks.value = tasks
        store.save(tasks)
    }
}
