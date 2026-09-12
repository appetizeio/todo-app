package io.appetize.todo

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Teal = Color(0xFF2DD4BF)
private val Ink = Color(0xFF0F1117)
private val Raised = Color(0xFF171A23)

// the app is dark regardless of the system setting, so a demo device that
// happens to be in light mode still shows the intended design
private val dark = darkColorScheme(
    primary = Teal,
    onPrimary = Color(0xFF04312B),
    primaryContainer = Teal,
    onPrimaryContainer = Color(0xFF04312B),
    background = Ink,
    surface = Raised,
    surfaceVariant = Color(0xFF212632),
    onSurface = Color(0xFFE8EAF0),
    onSurfaceVariant = Color(0xFF9AA2B4),
    error = Color(0xFFFF8A8A),
    secondaryContainer = Color(0xFF15382F),
    onSecondaryContainer = Teal,
    outline = Color(0xFF3A4152),
)

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = dark, content = content)
}
