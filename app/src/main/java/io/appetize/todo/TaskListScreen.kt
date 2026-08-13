package io.appetize.todo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val highlightedId by viewModel.highlightedId.collectAsState()
    val listState = rememberLazyListState()
    var adding by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }

    val visible = remember(tasks, filter) { viewModel.visible() }
    val done = tasks.count { it.done }

    // a todoapp://task/<id> link should bring that row into view
    LaunchedEffect(highlightedId, visible) {
        val index = visible.indexOfFirst { it.id == highlightedId }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Todo", fontWeight = FontWeight.SemiBold)
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { adding = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("New task") },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            ProgressHeader(done = done, total = tasks.size)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TaskFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filter == option,
                        onClick = { viewModel.setFilter(option) },
                        label = { Text(option.label) },
                    )
                }
            }

            if (visible.isEmpty()) {
                EmptyState(filter)
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp,
                        bottom = 96.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(visible, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            today = today,
                            highlighted = task.id == highlightedId,
                            onToggle = { viewModel.toggle(task.id) },
                            onRemove = { viewModel.remove(task.id) },
                        )
                    }

                    if (tasks.any { it.done }) {
                        item {
                            TextButton(onClick = viewModel::clearCompleted) {
                                Text("Clear completed")
                            }
                        }
                    }
                }
            }
        }
    }

    if (adding) {
        AddTaskSheet(
            onDismiss = { adding = false },
            onSave = { title, dueDate ->
                viewModel.add(title, dueDate)
                adding = false
            },
        )
    }
}

@Composable
private fun ProgressHeader(done: Int, total: Int) {
    val remaining = total - done

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (remaining == 0) "All done — nice work"
            else "$remaining of $total still to do",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else done.toFloat() / total },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EmptyState(filter: TaskFilter) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = when (filter) {
                TaskFilter.Done -> "Nothing completed yet."
                TaskFilter.Active -> "No tasks left. Enjoy it."
                TaskFilter.All -> "No tasks yet — add your first one."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TaskCard(
    task: Task,
    today: LocalDate,
    highlighted: Boolean,
    onToggle: () -> Unit,
    onRemove: () -> Unit,
) {
    // a border reads more cleanly than tinting an elevated card's fill
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = if (highlighted) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = task.done, onCheckedChange = { onToggle() })

            Column(modifier = Modifier.weight(1f).padding(vertical = 10.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration =
                        if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                    color =
                        if (task.done) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface,
                )
                task.dueDate?.let { due ->
                    DueDateLabel(task = task, due = due, today = today)
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete ${task.title}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DueDateLabel(task: Task, due: LocalDate, today: LocalDate) {
    val overdue = task.isOverdue(today)
    val dueToday = task.isDueToday(today)

    val colour = when {
        task.done -> MaterialTheme.colorScheme.onSurfaceVariant
        overdue -> MaterialTheme.colorScheme.error
        dueToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(top = 2.dp),
    ) {
        Icon(
            Icons.Outlined.DateRange,
            contentDescription = null,
            tint = colour,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = if (overdue) "Overdue · ${formatDueDate(due, today)}"
            else formatDueDate(due, today),
            style = MaterialTheme.typography.labelMedium,
            color = colour,
        )
    }
}
