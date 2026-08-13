package io.appetize.todo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        enableEdgeToEdge()

        val store = TaskStore(applicationContext)
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                TaskViewModel(store) as T
        }

        pendingLink.value = DeepLink.from(intent?.data)

        setContent {
            TodoTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                val link by pendingLink.collectAsState()

                LaunchedEffect(link) {
                    link?.let {
                        viewModel.apply(it)
                        pendingLink.value = null
                    }
                }

                TaskListScreen(viewModel)
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
