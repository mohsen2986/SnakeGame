import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.size
import androidx.compose.ui.unit.width
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    // Define the desired aspect ratio (width / height)
    // For a portrait handheld look, 0.6f (3:5) or 0.625f (10:16) works well
    val aspectRatio = 0.6f

    // Initialize the window state with a default size
    val state = rememberWindowState(size = DpSize(450.dp, 750.dp))

    // Keep track of the last size to detect changes and avoid infinite loops
    var lastSize by remember { mutableStateOf(state.size) }

    LaunchedEffect(state.size) {
        if (state.size != lastSize) {
            val widthChanged = state.size.width != lastSize.width
            val newSize = if (widthChanged) {
                // If width was changed by the user, adjust height to maintain ratio
                DpSize(state.size.width, (state.size.width.value / aspectRatio).dp)
            } else {
                // If height was changed, adjust width to maintain ratio
                DpSize((state.size.height.value * aspectRatio).dp, state.size.height)
            }
            state.size = newSize
            lastSize = newSize
        }
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Snake Game",
        state = state
    ) {
        App()
    }
}
