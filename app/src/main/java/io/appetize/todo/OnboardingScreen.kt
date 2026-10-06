package io.appetize.todo

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val kicker: String,
    val title: String,
    val body: String,
)

private val pages = listOf(
    OnboardingPage(
        kicker = "Welcome",
        title = "Everything due, in one list",
        body = "Todo keeps what is coming up, what slipped, and what you already finished in a single list.",
    ),
    OnboardingPage(
        kicker = "Due dates",
        title = "Late and today stand out",
        body = "Overdue tasks turn red. Anything due today is teal, so the next thing to do is obvious.",
    ),
    OnboardingPage(
        kicker = "Filters",
        title = "Show only what matters",
        body = "All, Active, Overdue, and Done are one tap away. Switching filters never changes the tasks themselves.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val lastPage = pages.lastIndex
    val onLastPage = pagerState.currentPage == lastPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .semantics { testTagsAsResourceId = true }
            .testTag("onboarding"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            if (!onLastPage) {
                TextButton(
                    onClick = onFinished,
                    modifier = Modifier.testTag("onboarding-skip"),
                ) {
                    Text(
                        "Skip",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { index ->
            OnboardingPageContent(page = pages[index], index = index)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            pages.indices.forEach { index ->
                val selected = pagerState.currentPage == index
                val width by animateDpAsState(if (selected) 22.dp else 6.dp, label = "dot")
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline,
                        ),
                )
            }
        }

        Button(
            onClick = {
                if (onLastPage) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 20.dp)
                .height(52.dp)
                .testTag("onboarding-next"),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = if (onLastPage) "Get started" else "Continue",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun OnboardingPageContent(page: OnboardingPage, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        // a fixed slot keeps the title in the same place on every page
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
            contentAlignment = Alignment.BottomStart,
        ) {
            when (index) {
                0 -> WelcomeMark()
                1 -> DueDatePreview()
                else -> FilterPreview()
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = page.kicker,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = page.title,
            modifier = Modifier.testTag("onboarding-title"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = page.body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun WelcomeMark() {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp),
        )
    }
}

@Composable
private fun DueDatePreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        ) {
            PreviewTask(
                title = "Review the release notes",
                meta = "Overdue · Yesterday",
                metaColor = MaterialTheme.colorScheme.error,
            )
            PreviewTask(
                title = "Send the standup summary",
                meta = "Today",
                metaColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun PreviewTask(
    title: String,
    meta: String,
    metaColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            modifier = Modifier.size(24.dp),
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = metaColor,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = meta,
                    style = MaterialTheme.typography.labelMedium,
                    color = metaColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FilterPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TaskFilter.entries.forEach { option ->
                FilterChip(
                    selected = option == TaskFilter.Overdue,
                    onClick = {},
                    label = { Text(option.label) },
                )
            }
        }
    }
}
