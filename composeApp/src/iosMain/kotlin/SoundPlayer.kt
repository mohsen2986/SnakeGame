import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionCategoryOptionMixWithOthers
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.Foundation.NSFileManager

@OptIn(ExperimentalForeignApi::class)
class IosSoundPlayer : SoundPlayer {
    private var pickupPlayer: AVAudioPlayer? = null
    private var gameOverPlayer: AVAudioPlayer? = null

    init {
        setupAudioSession()
    }

    private fun setupAudioSession() {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, AVAudioSessionCategoryOptionMixWithOthers, null)
            session.setActive(true, null)
        } catch (e: Exception) {
            println("Failed to set up AVAudioSession: ${e.message}")
        }
    }

    override fun playPickupSound() {
        if (pickupPlayer == null) {
            pickupPlayer = createPlayer("pickup", "wav")
        }
        playSound(pickupPlayer)
    }

    override fun playGameOverSound() {
        if (gameOverPlayer == null) {
            gameOverPlayer = createPlayer("gameover", "wav")
        }
        playSound(gameOverPlayer)
    }

    private fun playSound(player: AVAudioPlayer?) {
        player?.let {
            if (it.playing) {
                it.stop()
            }
            it.currentTime = 0.0
            it.play()
        } ?: println("Cannot play sound: player is null")
    }

    private fun createPlayer(name: String, extension: String): AVAudioPlayer? {
        val bundle = NSBundle.mainBundle
        val fileManager = NSFileManager.defaultManager
        val fileName = "$name.$extension"
        
        // Use a more thorough search strategy
        var foundPath: String? = null
        
        // 1. Try standard path first for performance
        val standardPath = bundle.pathForResource("compose-resources/files/$name", extension)
        if (standardPath != null) {
            foundPath = standardPath
        } else {
            // 2. Search recursively if not found in standard path
            val enumerator = fileManager.enumeratorAtPath(bundle.bundlePath)
            while (true) {
                val relativePath = enumerator?.nextObject() as? String ?: break
                if (relativePath.endsWith(fileName)) {
                    foundPath = "${bundle.bundlePath}/$relativePath"
                    break
                }
            }
        }

        if (foundPath == null) {
            println("Sound file not found: $fileName in bundle: ${bundle.bundlePath}")
            return null
        }

        val url = NSURL.fileURLWithPath(foundPath)
        return try {
            val player = AVAudioPlayer(contentsOfURL = url, error = null)
            player.prepareToPlay()
            player
        } catch (e: Exception) {
            println("Error creating AVAudioPlayer for $name at $foundPath: ${e.message}")
            null
        }
    }
}

@Composable
actual fun rememberSoundPlayer(): SoundPlayer {
    return remember { IosSoundPlayer() }
}
