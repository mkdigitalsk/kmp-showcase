package sk.mkdigital.kmpshowcase.presentation.component.ext

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = clickable(
    interactionSource = null,
    indication = null,
    onClick = onClick
)
