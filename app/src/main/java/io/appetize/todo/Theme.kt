package io.appetize.todo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Pink = Color(0xFFEC4899)
private val Slate = Color(0xFF14141F)

private val light = lightColorScheme(primary = Pink, onPrimary = Color.White)
private val dark = darkColorScheme(
    primary = Pink,
    onPrimary = Color.White,
    background = Slate,
    surface = Slate,
)

@Composable
fun TodoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) dark else light,
        content = content,
    )
}
