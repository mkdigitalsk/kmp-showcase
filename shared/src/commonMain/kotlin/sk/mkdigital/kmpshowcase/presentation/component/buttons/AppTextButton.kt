package sk.mkdigital.kmpshowcase.presentation.component.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import sk.mkdigital.kmpshowcase.presentation.foundation.appColorScheme
import sk.mkdigital.kmpshowcase.presentation.foundation.buttonProgressSize
import sk.mkdigital.kmpshowcase.presentation.foundation.buttonProgressStroke

@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(contentColor = color),
        enabled = !loading,
        content = {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(buttonProgressSize),
                    strokeWidth = buttonProgressStroke,
                    color = color
                )
            } else {
                Text(text = text, style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}


@Composable
fun AppTextButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    AppTextButton(
        text = text,
        onClick = onClick,
        color = MaterialTheme.colorScheme.primary,
        loading = loading,
        modifier = modifier,
    )
}

@Composable
fun AppTextButtonError(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
) {
    AppTextButton(
        text = text,
        modifier = modifier,
        onClick = onClick,
        loading = loading,
        color = MaterialTheme.appColorScheme.error
    )
}
