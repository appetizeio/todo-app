package io.appetize.todo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Tasks persist as JSON in SharedPreferences, so the app needs no database. */
class TaskStore(context: Context) {
    private val prefs = context.getSharedPreferences("todo", Context.MODE_PRIVATE)

    fun load(): List<Task> {
        val stored = prefs.getString(KEY, null) ?: return seed()
        return runCatching { parse(stored) }.getOrElse { seed() }
    }

    fun save(tasks: List<Task>) {
        val array = JSONArray()
        tasks.forEach { task ->
            array.put(
                JSONObject()
                    .put("id", task.id)
                    .put("title", task.title)
                    .put("done", task.done)
                    .put("due", task.dueDate?.toString() ?: JSONObject.NULL)
            )
        }
        prefs.edit().putString(KEY, array.toString()).apply()
    }

    private fun parse(stored: String): List<Task> {
        val array = JSONArray(stored)
        return (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            Task(
                id = item.getString("id"),
                title = item.getString("title"),
                done = item.optBoolean("done"),
                dueDate = item.optString("due")
                    .takeIf { it.isNotEmpty() && it != "null" }
                    ?.let(LocalDate::parse),
            )
        }
    }

    /** Enough tasks, across enough due dates, that the list scrolls and every
     *  due-date state is visible on a first run. */
    private fun seed(): List<Task> {
        val today = LocalDate.now()
        return listOf(
            Task("deeplink", "Open a task with a deep link", dueDate = today.minusDays(2)),
            Task("review", "Review the Q3 release notes", dueDate = today.minusDays(1)),
            Task("standup", "Send the standup summary", dueDate = today),
            Task("designs", "Sign off the onboarding designs", dueDate = today),
            Task("interview", "Prep the candidate interview", dueDate = today.plusDays(1)),
            Task("expenses", "File March expenses", dueDate = today.plusDays(3)),
            Task("roadmap", "Draft the roadmap one-pager", dueDate = today.plusDays(7)),
            Task("offsite", "Book flights for the offsite", dueDate = today.plusDays(12)),
            Task("inbox", "Clear the shared inbox"),
            Task("swipe", "Swipe through a live device on Appetize", done = true, dueDate = today.minusDays(3)),
            Task("upload", "Upload a build to Appetize", done = true),
        )
    }

    private companion object {
        const val KEY = "tasks"
    }
}
