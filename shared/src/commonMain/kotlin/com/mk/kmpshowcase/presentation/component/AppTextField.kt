package com.mk.kmpshowcase.presentation.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.mk.kmpshowcase.presentation.foundation.appColorScheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    showClearButton: Boolean = true,
    onClear: (() -> Unit)? = null,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val clearButton: @Composable (() -> Unit)? = when {
        trailingIcon != null -> trailingIcon
        showClearButton && value.isNotEmpty() -> {
            {
                IconButton(
                    onClick = {
                        onValueChange("")
                        onClear?.invoke()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.appColorScheme.neutral80
                    )
                }
            }
        }

        else -> null
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyMedium,
        label = label?.let { { Text(text = it, style = MaterialTheme.typography.bodySmall) } },
        placeholder = placeholder?.let { { Text(text = it, style = MaterialTheme.typography.bodyMedium) } },
        supportingText = supportingText?.let { { Text(text = it, style = MaterialTheme.typography.bodySmall) } },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = clearButton,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.appColorScheme.neutral40,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.appColorScheme.neutral60,
            focusedPlaceholderColor = MaterialTheme.appColorScheme.neutral60,
            unfocusedPlaceholderColor = MaterialTheme.appColorScheme.neutral60,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}
