@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package snakegame.shared.generated.resources

import kotlin.OptIn
import kotlin.String
import kotlin.collections.MutableMap
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi

private object CommonMainDrawable0 {
  public val speaker: DrawableResource by 
      lazy { init_speaker() }
}

@InternalResourceApi
internal fun _collectCommonMainDrawable0Resources(map: MutableMap<String, DrawableResource>) {
  map.put("speaker", CommonMainDrawable0.speaker)
}

internal val Res.drawable.speaker: DrawableResource
  get() = CommonMainDrawable0.speaker

private fun init_speaker(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:speaker",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/snakegame.shared.generated.resources/drawable/speaker.xml", -1, -1),
    )
)
