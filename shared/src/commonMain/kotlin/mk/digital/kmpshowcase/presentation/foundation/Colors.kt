package mk.digital.kmpshowcase.presentation.foundation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val Neutral0Light: Color = Color(0xFFFFFFFF)
internal val Neutral20Light: Color = Color(0xFFC8C8C8)
internal val Neutral40Light: Color = Color(0xFF919191)
internal val Neutral80Light: Color = Color(0xFF232323)
internal val Neutral100Light: Color = Color.Black

internal val PrimaryLight: Color = Color(0xFF6200EE)
internal val PrimaryContainerLight: Color = Color(0xFF3700B3)
internal val SecondaryLight: Color = Color(0xFF03DAC6)
internal val SecondaryContainerLight: Color = Color(0xFF018786)
internal val BackgroundLight: Color = Neutral0Light
internal val SurfaceLight: Color = Neutral0Light
internal val ErrorLight: Color = Color(0xFFFF1A1A)
internal val ErrorContainerLight: Color = Color(0xFFF9DEDC)
internal val Transparent: Color = Color(0x00)


data class AppColors(
    val material: ColorScheme,
    val neutral0: Color,
    val neutral20: Color,
    val neutral80: Color,
    val neutral100: Color,
    val transparent: Color = Transparent,
) {
    val primary: Color get() = material.primary
}
