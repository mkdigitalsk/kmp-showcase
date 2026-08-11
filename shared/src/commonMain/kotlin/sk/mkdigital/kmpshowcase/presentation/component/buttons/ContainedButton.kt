package sk.mkdigital.kmpshowcase.presentation.component.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import sk.mkdigital.kmpshowcase.presentation.foundation.buttonProgressSize
import sk.mkdigital.kmpshowcase.presentation.foundation.buttonProgressStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import sk.mkdigital.kmpshowcase.presentation.foundation.cardCornerRadius6
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContainedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(space4),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(cardCornerRadius6)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(buttonProgressSize),
                strokeWidth = buttonProgressStroke,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun ContainedButton(
    id: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ContainedButton(text = stringResource(resource = id), onClick = onClick, modifier = modifier)
}
