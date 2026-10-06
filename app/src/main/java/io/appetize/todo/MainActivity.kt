package io.appetize.todo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    /**
     * A link can arrive before composition has produced a view model — a cold
     * start straight into `todoapp://…` does exactly that — so it waits here
     * until something is listening.
     */
    private val pendingLink = MutableStateFlow<DeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // the app is always dark, so the bars need light icons even on a
        // device whose system theme is light
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        val store = TaskStore(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TaskViewModel(store) as T
        }

        pendingLink.value = DeepLink.from(intent?.data)
        // a launch URL is a demo entry point, so it should not wait on first-run
        val showOnboardingFirst = !store.isOnboardingComplete() && pendingLink.value == null

        setContent {
            TodoTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val link by pendingLink.collectAsState()
                var onboardingComplete by remember { mutableStateOf(!showOnboardingFirst) }

                LaunchedEffect(link) {
                    link?.let {
                        viewModel.apply(it)
                        pendingLink.value = null
                    }
                }

                if (onboardingComplete) {
                    TaskListScreen(viewModel)
                } else {
                    OnboardingScreen(
                        onFinished = {
                            store.completeOnboarding()
                            onboardingComplete = true
                        },
                    )
                }
            }
        }
    }

    // singleTask means a second link arrives here rather than in onCreate
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingLink.value = DeepLink.from(intent.data)
    }
}
