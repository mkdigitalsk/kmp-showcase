package mk.digital.kmpshowcase.presentation.component.text.bodyMedium

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import mk.digital.kmpshowcase.presentation.foundation.appColors

@Composable
fun TextBodyMediumNeutral80(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    TextBodyMedium(
        text = text,
        modifier = modifier,
        color = MaterialTheme.appColors.neutral80,
        textAlign = textAlign,
        fontWeight = FontWeight.Normal,
        lineHeight = lineHeight,
    )
}
