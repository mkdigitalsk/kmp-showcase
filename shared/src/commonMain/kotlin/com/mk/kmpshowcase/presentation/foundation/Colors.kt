package com.mk.kmpshowcase.presentation.foundation

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.mk.designsystem.BrandBlue
import com.mk.designsystem.BrandNavy
import com.mk.designsystem.BrandTealDark
import com.mk.designsystem.DarkBrandBar
import com.mk.designsystem.DarkError
import com.mk.designsystem.DarkNeutral0
import com.mk.designsystem.DarkNeutral100
import com.mk.designsystem.DarkNeutral20
import com.mk.designsystem.DarkNeutral40
import com.mk.designsystem.DarkNeutral60
import com.mk.designsystem.DarkNeutral80
import com.mk.designsystem.DarkOnBrandBar
import com.mk.designsystem.DarkPrimary
import com.mk.designsystem.DarkSecondary
import com.mk.designsystem.DarkSuccess
import com.mk.designsystem.DarkWarning
import com.mk.designsystem.LightBrandBar
import com.mk.designsystem.LightError
import com.mk.designsystem.LightNeutral0
import com.mk.designsystem.LightNeutral100
import com.mk.designsystem.LightNeutral20
import com.mk.designsystem.LightNeutral40
import com.mk.designsystem.LightNeutral60
import com.mk.designsystem.LightNeutral80
import com.mk.designsystem.LightOnBrandBar
import com.mk.designsystem.LightPrimary
import com.mk.designsystem.LightSecondary
import com.mk.designsystem.LightSuccess
import com.mk.designsystem.LightWarning

// Brand colors composed from the design system (com.mk.designsystem), not defined here.

val Neutral0Light: Color = LightNeutral0
internal val Neutral20Light: Color = LightNeutral20
internal val Neutral40Light: Color = LightNeutral40
internal val Neutral60Light: Color = LightNeutral60
internal val Neutral80Light: Color = LightNeutral80
internal val Neutral100Light: Color = LightNeutral100

internal val PrimaryLight: Color = LightPrimary
internal val PrimaryContainerLight: Color = BrandBlue
internal val SecondaryLight: Color = LightSecondary
internal val SecondaryContainerLight: Color = BrandTealDark
internal val BackgroundLight: Color = Neutral0Light
internal val SurfaceLight: Color = Neutral0Light
internal val ErrorLight: Color = LightError
internal val ErrorContainerLight: Color = Color(0xFFF9DEDC)
internal val SuccessLight: Color = LightSuccess
internal val WarningLight: Color = LightWarning
internal val BrandBarLight: Color = LightBrandBar
internal val OnBrandBarLight: Color = LightOnBrandBar

internal val Neutral0Dark: Color = DarkNeutral0
internal val Neutral20Dark: Color = DarkNeutral20
internal val Neutral40Dark: Color = DarkNeutral40
internal val Neutral60Dark: Color = DarkNeutral60
internal val Neutral80Dark: Color = DarkNeutral80
internal val Neutral100Dark: Color = DarkNeutral100

internal val PrimaryDark: Color = DarkPrimary
internal val PrimaryContainerDark: Color = BrandNavy
internal val SecondaryDark: Color = DarkSecondary
internal val SecondaryContainerDark: Color = BrandTealDark
internal val BackgroundDark: Color = Neutral0Dark
internal val SurfaceDark: Color = Color(0xFF1E1E1E)
internal val ErrorDark: Color = DarkError
internal val ErrorContainerDark: Color = Color(0xFF93000A)
internal val SuccessDark: Color = DarkSuccess
internal val WarningDark: Color = DarkWarning
internal val BrandBarDark: Color = DarkBrandBar
internal val OnBrandBarDark: Color = DarkOnBrandBar

internal val Transparent: Color = Color(0x00)


data class AppColors(
    val material: ColorScheme,
    val neutral0: Color,
    val neutral20: Color,
    val neutral40: Color,
    val neutral60: Color,
    val neutral80: Color,
    val neutral100: Color,
    val success: Color,
    val warning: Color,
    val brandBar: Color,
    val onBrandBar: Color,
    val transparent: Color = Transparent,
) {
    val primary: Color get() = material.primary
    val error: Color get() = material.error
}
