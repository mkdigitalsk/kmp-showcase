package com.mk.kmpshowcase.presentation.component.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mk.kmpshowcase.presentation.foundation.cardCornerRadius6
import com.mk.kmpshowcase.presentation.foundation.space4
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ContainedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(space4),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        enabled = enabled,
        shape = RoundedCornerShape(cardCornerRadius6)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
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
