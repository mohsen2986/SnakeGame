import androidx.compose.runtime.Composable

interface SoundPlayer {
    fun playPickupSound()
    fun playGameOverSound()
}

@Composable
expect fun rememberSoundPlayer(): SoundPlayer
