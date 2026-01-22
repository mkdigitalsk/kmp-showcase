package mk.digital.kmpshowcase.presentation.component.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mk.digital.kmpshowcase.presentation.component.text.labelLarge.TextButtonNeutral0
import mk.digital.kmpshowcase.presentation.foundation.cardCornerRadius6
import mk.digital.kmpshowcase.presentation.foundation.space4


@Composable
fun ContainedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(space4),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        enabled = enabled,
        shape = RoundedCornerShape(cardCornerRadius6)
    ) {
        TextButtonNeutral0(text = text)
    }
}
