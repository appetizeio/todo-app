package io.appetize.todo

import android.net.Uri
import java.time.LocalDate

/**
 * What a `todoapp://` link asks the app to do. Parsed from the launch intent so
 * an Appetize session can start the app straight into one of these.
 */
sealed interface DeepLink {
    /** `todoapp://task/<id>` — scroll to and highlight one task. */
    data class OpenTask(val id: String) : DeepLink

    /** `todoapp://new?title=Buy%20milk&due=2026-09-01` — both params optional. */
    data class NewTask(val title: String?, val dueDate: LocalDate?) : DeepLink

    /** `todoapp://tasks` — the plain list. */
    data object List : DeepLink

    companion object {
        fun from(uri: Uri?): DeepLink? {
            if (uri?.scheme != "todoapp") {
                return null
            }

            // todoapp://task/1 puts "task" in the host and "/1" in the path
            val segments = uri.pathSegments.orEmpty()
            return when (uri.host) {
                "task" -> segments.firstOrNull()?.let(::OpenTask)
                "new" -> NewTask(
                    title = uri.getQueryParameter("title"),
                    // a malformed date shouldn't stop the task being created
                    dueDate = uri.getQueryParameter("due")
                        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
                )

                "tasks" -> List
                else -> null
            }
        }
    }
}
