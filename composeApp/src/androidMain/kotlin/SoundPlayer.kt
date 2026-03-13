import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidSoundPlayer(private val context: android.content.Context) : SoundPlayer {
    override fun playPickupSound() {
        play("pickup.wav")
    }

    override fun playGameOverSound() {
        play("gameover.wav")
    }

    private fun play(fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Adjusting path for Compose Multiplatform resources on Android
                val assetFileDescriptor = context.assets.openFd("composeResources/snakegame.composeapp.generated.resources/files/$fileName")
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                assetFileDescriptor.close()
                mediaPlayer.prepare()
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    it.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    val context = LocalContext.current
    return remember(context) { AndroidSoundPlayer(context) }
}
