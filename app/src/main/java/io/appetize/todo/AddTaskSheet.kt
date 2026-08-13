package io.appetize.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onSave: (String, LocalDate?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var pickingDate by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "New task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What needs doing?") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SuggestionChip(
                    onClick = { pickingDate = true },
                    icon = {
                        Icon(
                            Icons.Outlined.DateRange,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    },
                    label = {
                        Text(dueDate?.let(::formatDueDate) ?: "Set a due date")
                    },
                )
                if (dueDate != null) {
                    TextButton(onClick = { dueDate = null }) {
                        Text("Clear")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(
                    onClick = { onSave(title, dueDate) },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("Add task")
                }
            }
        }
    }

    if (pickingDate) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate
                ?.atStartOfDay(ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { pickingDate = false },
            confirmButton = {
                TextButton(onClick = {
                    dueDate = pickerState.selectedDateMillis?.let { millis ->
                        Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    pickingDate = false
                }) {
                    Text("Select")
                }
            },
            dismissButton = {
                TextButton(onClick = { pickingDate = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}
