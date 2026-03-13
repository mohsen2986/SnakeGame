import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.document
import org.w3c.dom.HTMLAudioElement
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
class WasmSoundPlayer : SoundPlayer {
    override fun playPickupSound() {
        play("pickup.wav")
    }

    override fun playGameOverSound() {
        play("gameover.wav")
    }

    private fun play(fileName: String) {
        try {
            val audio = document.createElement("audio") as HTMLAudioElement
            // The path for Compose Multiplatform resources on WASM
            audio.src = "composeResources/snakegame.composeapp.generated.resources/files/$fileName"
            audio.play()
        } catch (e: Exception) {
            println("Error playing sound $fileName: ${e.message}")
        }
    }
}

@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    return remember { WasmSoundPlayer() }
}
