import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import java.io.BufferedInputStream

class DesktopSoundPlayer : SoundPlayer {
    override fun playPickupSound() {
        play("pickup.wav")
    }

    override fun playGameOverSound() {
        play("gameover.wav")
    }

    private fun play(fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Compose Multiplatform Desktop resource path
                val resourcePath = "composeResources/snakegame.composeapp.generated.resources/files/$fileName"
                val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
                if (inputStream != null) {
                    val bufferedIn = BufferedInputStream(inputStream)
                    val audioStream = AudioSystem.getAudioInputStream(bufferedIn)
                    val clip = AudioSystem.getClip()
                    clip.open(audioStream)
                    clip.start()
                    clip.addLineListener { event ->
                        if (event.type == LineEvent.Type.STOP) {
                            clip.close()
                        }
                    }
                } else {
                    println("Sound file not found: $resourcePath")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    return remember { DesktopSoundPlayer() }
}
