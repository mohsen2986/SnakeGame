package com.example.snakegame.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val yellow = Color(0xFFf5be01)
val darkGray = Color(0xFF4a4b4f)

val dividerColor = Color(0xFF904e07)
val itemColor = Color(0XFF564d50)

val a = Color(0xffd59300)
val b = Color(0xffe2a300)

val colors =
    listOf(
        a ,
        b,
        a ,
        b,
        yellow ,
        yellow ,
    )

val HoleBrush = Brush.linearGradient(colors)

val colorst =
    listOf(
        dividerColor ,
        b,
        a ,
        yellow ,
        yellow ,
        b,
        a ,
        dividerColor ,
    )

val HoleBrusht = Brush.verticalGradient(colorst)



val colors2 =
    listOf(
        a ,
        b ,
        yellow ,
    )

val TwoHolesBrush = Brush.linearGradient(colors2 ,
    start = Offset(0f, 0f),
    end = Offset(40f, 240f)
)



val c = Color(0xFFcdbe9d)
val stroke = listOf(
    Color.White,
    c ,
    c ,
    darkGray,
    darkGray,

    )
val ButtonLightShadowBrush = Brush.linearGradient(stroke)


val stroke2 = listOf(
    Color.White,
    c ,
    c ,
    darkGray,
    darkGray,
    darkGray,
    darkGray,
    )
val ButtonLightShadowBrush2 = Brush.linearGradient(stroke2)

val stroke3 = listOf(
    c ,
    Color.White,
    darkGray,
    darkGray,
    darkGray,
)
val ButtonLightShadowBrush3 = Brush.sweepGradient(stroke3)

val stroke4 = listOf(
    darkGray,
    darkGray,
    Color.White,
    darkGray,
    darkGray,
    darkGray,
)
val ButtonLightShadowBrush4 = Brush.sweepGradient(stroke4)