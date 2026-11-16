package sk.mkdigital.kmpshowcase.presentation.component.dividers

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.graphics.Color

@Composable
@NonRestartableComposable
private fun AppDivider(color: Color) {
    HorizontalDivider(color = color)
}

@Composable
fun AppDividerPrimary() {
    AppDivider(color = MaterialTheme.colorScheme.primary)
}


