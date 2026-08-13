# todo-app

A small Android to-do app, built to demonstrate Appetize features on a real
device — including deep links.

Jetpack Compose, Material 3, no backend. Tasks persist locally, so they survive
an app restart within a session.

Create tasks with a due date, filter by All / Active / Done, and watch overdue
and due-today tasks colour themselves. Seeded with enough tasks that the list
scrolls on first run.

- **Package** `io.appetize.todo`
- **Minimum** Android 10 (API 29) · **Target** Android 15 (API 35)
- **Size** ~2 MB, single universal APK, no split set

## Deep links

The app registers the `todoapp` scheme — not a bare `todo`, which is likely
enough to collide with another installed app that Android would show a chooser
instead of launching, ruining a demo. Pass one of these as the launch URL of an
Appetize session, or send it to a running device.

| Link | Behaviour |
| --- | --- |
| `todoapp://tasks` | Opens the list |
| `todoapp://task/<id>` | Opens the list, scrolls to that task and highlights it |
| `todoapp://new?title=Buy%20milk` | Adds a task with that title |
| `todoapp://new?title=Ship%20it&due=2026-09-01` | Adds a task with a due date (`YYYY-MM-DD`) |

The seeded tasks use stable ids, so `todoapp://task/deeplink` works on a fresh
install without knowing a generated id.

Testing on a local emulator:

```bash
adb shell am start -a android.intent.action.VIEW -d "todoapp://task/deeplink"

# escape the & — adb hands the string to the device shell, which would
# otherwise treat it as a command separator and silently drop the due date
adb shell am start -a android.intent.action.VIEW -d "todoapp://new?title=Ship%20it\&due=2026-09-01"
```

`MainActivity` is `singleTask`, so a link sent to an already-running app arrives
in `onNewIntent` and is applied without restarting it.

## Build

Needs JDK 17+ and the Android SDK (platform 35, build-tools 35.0.0).

```bash
./gradlew assembleRelease     # app/build/outputs/apk/release/app-release.apk
```

Release builds are signed with the standard Android debug key on purpose: the
APK is a public demo artifact and must be installable by anyone without a
private signing key. Do not treat it as a distributable product build.

## Layout

```
app/src/main/java/io/appetize/todo/
  MainActivity.kt      hosts Compose, routes incoming deep links
  DeepLink.kt          parses todoapp:// URIs
  TaskViewModel.kt     task state, filtering and sorting
  TaskStore.kt         JSON persistence in SharedPreferences
  TaskListScreen.kt    list, progress header, filters
  AddTaskSheet.kt      new-task sheet with the date picker
  DueDate.kt           due date formatting
  Task.kt              the model
  Theme.kt             Material 3 colours
```
