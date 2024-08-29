import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import snakegame.shared.generated.resources.Res
import snakegame.shared.generated.resources.speaker
import kotlin.random.Random



data class State(val food: Pair<Int, Int>, val snake: List<Pair<Int, Int>>)

class Game(private val scope: CoroutineScope) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(State(food = Pair(0, 5), snake = listOf(Pair(7, 7))))
    val state: StateFlow<State> = mutableState.asStateFlow()

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }

    init {
        scope.launch {
            var snakeLength = 4

            while (true) {
                delay(150)
                mutableState.update {


                    val newPosition = it.snake.first().let { poz ->
                        mutex.withLock {
                            Pair(
                                (poz.first + move.first + X_BOARD_SIZE) % X_BOARD_SIZE,
                                (poz.second + move.second + Y_BOARD_SIZE) % Y_BOARD_SIZE
                            )
                        }
                    }
                    if (newPosition == it.food) {
                        snakeLength++
                    }
                    if (it.snake.contains(newPosition)) {
                        snakeLength = 1
                    }

                    it.copy(
                        food = if (newPosition == it.food)
                            Pair(Random.nextInt(X_BOARD_SIZE), Random.nextInt(Y_BOARD_SIZE))
                        else it.food,
                        snake = listOf(newPosition) + it.snake.take(snakeLength - 1)
                    )
                }
            }
        }

    }

    companion object {
        const val X_BOARD_SIZE = 16
        const val Y_BOARD_SIZE = 10
    }
}

@Composable
fun Snake(game: Game) {
    val state = game.state.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.value.let {
            Board(state = it)
        }
        Buttons {
            game.move = it
        }
    }

}

@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = Modifier.size(64.dp)
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onDirectionChange(Pair(0, -1)) }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)
        }
        Row {
            Button(onClick = { onDirectionChange(Pair(-1, 0)) }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
            }

            Spacer(modifier = buttonSize)

            Button(onClick = { onDirectionChange(Pair(1, 0)) }, modifier = buttonSize) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
        }
        Button(onClick = { onDirectionChange(Pair(0, 1)) }, modifier = buttonSize) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
        }
    }
}

@Composable
fun Board(state: State) {
    BoxWithConstraints(
        modifier = Modifier.padding(16.dp)
    ) {

        val titleSizeX = maxWidth / Game.X_BOARD_SIZE
        val titleSizeY = maxHeight / Game.Y_BOARD_SIZE

        Box(
            modifier = Modifier
                .size(maxWidth)
                .border(2.dp, Color.DarkGray)
        )
        Box(
            modifier = Modifier
                .offset(
                    x = titleSizeX * state.food.first,
                    y = titleSizeY * state.food.second
                )
                .size(titleSizeX ,titleSizeY)
                .background(
                    Color(0XFFf60022), CircleShape
                )
        )

        state.snake.forEach {
            Box(
                modifier = Modifier
                    .offset(
                        x = titleSizeX * it.first,
                        y = titleSizeY * it.second
                    )
                    .size(titleSizeX ,titleSizeY)
                    .background(
                        Color.DarkGray, Shapes().small
                    )
            )
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
//    GameBoard()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GameBoard(game: Game) {
    val state = game.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .windowInsetsPadding(WindowInsets.safeDrawing)

    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5))
                .background(yellow)
                .padding(bottom = 35.dp, top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 10.dp)
                    .clip(CircleShape.copy(CornerSize(5)))
                    .background(Color.Black)

            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(top = 10.dp, start = 15.dp, end = 15.dp)
                        .clip(CircleShape.copy(CornerSize(5)))
                        .background(Color(0xFFfdf4f7))


                ) {
                    state.value.let {
                        Board(state = it)
                    }
                }

                Title(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 5.dp))
            }
        }

        val width = 75.dp
        Row {
            Box(
                modifier = Modifier
                    .height(width)
                    .weight(1f)
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(15, 0, 0, 15))
                    .background(HoleBrusht)
            )

            Spacer(
                modifier = Modifier
                    .size(1.dp)
                    .background(dividerColor)
            )

            Box(
                modifier = Modifier
                    .height(width)
                    .weight(3f)
                    .background(HoleBrusht)
            )
            Spacer(
                modifier = Modifier
                    .size(1.dp)
                    .background(dividerColor)
            )

            Box(
                modifier = Modifier
                    .height(width)
                    .padding(top = 5.dp)
                    .weight(2f)
                    .background(HoleBrusht)
            )

            Spacer(
                modifier = Modifier
                    .size(1.dp)
                    .background(dividerColor)
            )

            Box(
                modifier = Modifier
                    .height(width)
                    .weight(1f)
                    .background(HoleBrusht)
            )
            Spacer(
                modifier = Modifier
                    .size(1.dp)
                    .background(dividerColor)
            )

            Box(
                modifier = Modifier
                    .height(width)
                    .weight(0.5f)
                    .padding(top = 5.dp)
                    .clip(RoundedCornerShape(0, 15, 15, 0))
                    .background(HoleBrusht)
            )

        }



        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(yellow)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(HoleBrusht)
            )

            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .border(
                        brush = HoleBrush,
                        width = 7.dp,
                        shape = CircleShape
                    )
                    .align(Alignment.CenterHorizontally)

            ) {

                FilledIconButton(
                    modifier = Modifier
                        .padding(9.dp)
                        .border(BorderStroke(2.dp, ButtonLightShadowBrush), CircleShape)
                        .size(30.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = darkGray),
                    onClick = { /*TODO*/ }
                ) {
                    Icon(imageVector = Icons.Filled.Star, contentDescription = null)
                }
            }





            Row {

                Box(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .border(
                            brush = HoleBrush,
                            width = 13.dp,
                            shape = CircleShape
                        )
                        .padding(10.dp)

                ) {

                    Column(
                        modifier = Modifier.padding(15.dp)
                    ) {

                        val buttonWidthSize = 40.dp
                        val buttonCorner = 4.dp
                        val strokeHoleSize = 5.dp

                        Box(
                            modifier = Modifier
                                .size(
                                    height = buttonWidthSize + strokeHoleSize,
                                    width = buttonWidthSize + (strokeHoleSize * 2)
                                )
                                .background(
                                    brush = HoleBrush,
                                    shape = RoundedCornerShape(
                                        topStart = buttonCorner,
                                        topEnd = buttonCorner
                                    )
                                )
                                .align(Alignment.CenterHorizontally)

                        ) {

                            CrossButton(
                                RoundedCornerShape(buttonCorner, buttonCorner),
                                buttonWidthSize,
                                Modifier
                                    .size(buttonWidthSize)
                                    .border(BorderStroke(2.dp, ButtonLightShadowBrush2))
                                    .align(Alignment.BottomCenter)
                                    .clickable {
                                        game.move = 0 to -1
                                    }
                            )
                        }

                        Row {

                            Box(
                                modifier = Modifier
                                    .size(
                                        height = buttonWidthSize + (strokeHoleSize * 2),
                                        width = buttonWidthSize + (strokeHoleSize)
                                    )
                                    .background(
                                        brush = HoleBrush,
                                        shape = RoundedCornerShape(
                                            topStart = buttonCorner,
                                            bottomStart = buttonCorner
                                        )
                                    )

                            ) {
                                CrossButton(
                                    RoundedCornerShape(
                                        topStart = buttonCorner,
                                        bottomStart = buttonCorner
                                    ),
                                    buttonWidthSize,
                                    Modifier
                                        .border(BorderStroke(2.dp, ButtonLightShadowBrush2))
                                        .align(Alignment.CenterEnd)
                                        .clickable {
                                            game.move = -1 to 0
                                        },
                                )

                            }


                            Spacer(
                                modifier = Modifier
                                    .size(buttonWidthSize, buttonWidthSize + (strokeHoleSize * 2))
                                    .background(darkGray),
                            )

                            Box(
                                modifier = Modifier
                                    .size(
                                        height = buttonWidthSize + (strokeHoleSize * 2),
                                        width = buttonWidthSize + (strokeHoleSize)
                                    )
                                    .background(
                                        brush = HoleBrush,
                                        shape = RoundedCornerShape(
                                            topEnd = buttonCorner,
                                            bottomStart = buttonCorner
                                        )
                                    )

                            ) {
                                CrossButton(
                                    RoundedCornerShape(
                                        topEnd = buttonCorner,
                                        bottomEnd = buttonCorner
                                    ),
                                    buttonWidthSize,
                                    Modifier
                                        .drawBehind {
                                            val strokeWidthPx = 2.dp.toPx()

                                            drawPath(
                                                Path().apply {
                                                    moveTo(0f, 0f)
                                                    lineTo(strokeWidthPx, strokeWidthPx)
                                                    val width = size.width
                                                    lineTo(width - strokeWidthPx, strokeWidthPx)
                                                    lineTo(width, 0f)
                                                    close()
                                                },
                                                color = Color.White
                                            )
                                        }
                                        .align(Alignment.CenterStart)
                                        .clickable {
                                            game.move = 1 to 0
                                        }

                                )
                            }


                        }

                        Box(
                            modifier = Modifier
                                .size(
                                    height = buttonWidthSize + (strokeHoleSize),
                                    width = buttonWidthSize + (strokeHoleSize * 2)
                                )
                                .background(
                                    brush = HoleBrush,
                                    shape = RoundedCornerShape(
                                        bottomEnd = buttonCorner,
                                        bottomStart = buttonCorner
                                    )
                                )
                                .align(Alignment.CenterHorizontally)

                        ) {
                            CrossButton(
                                RoundedCornerShape(
                                    bottomEnd = buttonCorner,
                                    bottomStart = buttonCorner
                                ),
                                buttonWidthSize,
                                Modifier
                                    .drawBehind {
                                        val strokeWidthPx = 2.dp.toPx()

                                        drawPath(
                                            Path().apply {
                                                moveTo(0f, 0f)
                                                lineTo(strokeWidthPx, strokeWidthPx)
                                                val height = size.height
                                                lineTo(strokeWidthPx, height - strokeWidthPx)
                                                lineTo(0f, height)
                                                close()
                                            },
                                            color = Color.White
                                        )
                                    }
                                    .align(Alignment.TopCenter)
                                    .clickable {
                                        game.move = 0 to 1
                                    }
                            )

                        }

                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .padding(top = 25.dp, end = 12.dp)
                        .rotate(-15f)
                        .border(BorderStroke(6.dp, TwoHolesBrush), CircleShape)
                        .padding(12.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .border(
                                brush = HoleBrush,
                                width = 5.dp,
                                shape = CircleShape
                            )
                    ) {

                        FilledIconButton(
                            modifier = Modifier
                                .padding(7.dp)
                                .size(45.dp)
                                .border(BorderStroke(2.dp, ButtonLightShadowBrush), CircleShape),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = darkGray),
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.size(20.dp))


                    Box(
                        modifier = Modifier
                            .border(
                                brush = HoleBrush,
                                width = 5.dp,
                                shape = CircleShape
                            )
                    ) {

                        FilledIconButton(
                            modifier = Modifier
                                .padding(7.dp)
                                .size(45.dp)
                                .border(BorderStroke(2.dp, ButtonLightShadowBrush), CircleShape),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = darkGray),
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null)
                        }
                    }
                }
            }




            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .size(85.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(Res.drawable.speaker),
                contentDescription = null
            )
        }
    }
}

@Composable
fun Title(
    modifier: Modifier,
) {
    val currentFontSizePx = with(LocalDensity.current) { 16.dp.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 2

    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
    )

    val gradientColors = listOf(Color(0XFF00FFFF), Color(0XFFadd8e6), Color(0XFFA020F0))

    val brush = Brush.linearGradient(
        gradientColors,
        start = Offset(offset, offset),
        end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
        tileMode = TileMode.Mirror,
    )

    Text(
        modifier = modifier,
        text = "Pixel Snake",
        fontSize = 22.sp,
        style = TextStyle(
            brush = brush,
        ),
    )
}

@Composable
fun CrossButton(
    bgShape: Shape,
    buttonSize: Dp,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(buttonSize)
            .background(darkGray, bgShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White), // Indication is handled above
                onClick = {}
            )
            .then(modifier),
    ) {

    }

}

