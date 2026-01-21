package mk.digital.kmpshowcase.presentation.component.text.labelLarge

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import mk.digital.kmpshowcase.presentation.foundation.appColors

@Composable
internal fun TextLabelLarge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    Text(
        modifier = modifier,
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = color,
        fontWeight = FontWeight.Medium,
        textAlign = textAlign
    )
}

@Composable
internal fun TextButtonPrimary(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    TextLabelLarge(
        modifier = modifier,
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.primary,
        textAlign = textAlign
    )
}

@Composable
internal fun TextButtonNeutral0(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
) {
    TextLabelLarge(
        modifier = modifier,
        text = text.uppercase(),
        color = MaterialTheme.appColors.neutral0,
        textAlign = textAlign
    )
}
