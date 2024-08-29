import androidx.compose.runtime.*
import kotlinx.coroutines.GlobalScope

@Composable
fun App()  {
    val lifecycleScope = rememberCoroutineScope()
    val game = Game(lifecycleScope)

    GameBoard(game)
}