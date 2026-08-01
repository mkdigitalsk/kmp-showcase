package com.mk.kmpshowcase.presentation.component.biometric

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mk.kmpshowcase.presentation.component.image.AppIconPrimary
import com.mk.kmpshowcase.presentation.component.text.bodySmall.TextBodySmallNeutral80
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.login_biometric_hint_faceid
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun BiometricView(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp)
        ) {
            AppIconPrimary(
                imageVector = Icons.Filled.Face,
                contentDescription = stringResource(Res.string.login_biometric_hint_faceid),
                size = 48.dp,
            )
        }
        TextBodySmallNeutral80(stringResource(Res.string.login_biometric_hint_faceid))
    }
}
