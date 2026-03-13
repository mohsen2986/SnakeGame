import androidx.compose.runtime.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun App()  {
    val lifecycleScope = rememberCoroutineScope()
    val game = remember { Game(lifecycleScope) }
    val soundPlayer = rememberSoundPlayer()

    LaunchedEffect(game) {
        game.events.collectLatest { event ->
            when (event) {
                Game.SoundEvent.PICKUP -> soundPlayer.playPickupSound()
                Game.SoundEvent.GAME_OVER -> soundPlayer.playGameOverSound()
            }
        }
    }

    GameBoard(game)
}
