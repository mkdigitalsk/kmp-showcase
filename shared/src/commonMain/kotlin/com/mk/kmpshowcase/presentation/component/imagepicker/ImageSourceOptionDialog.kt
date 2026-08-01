package com.mk.kmpshowcase.presentation.component.imagepicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.mk.kmpshowcase.presentation.component.AppAlertDialog
import com.mk.kmpshowcase.presentation.component.image.AppIconPrimary
import com.mk.kmpshowcase.presentation.component.spacers.RowSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral100
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.imagepicker_camera
import com.mk.kmpshowcase.shared.generated.resources.imagepicker_gallery
import com.mk.kmpshowcase.shared.generated.resources.imagepicker_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImageSourceOptionDialog(
    title: String = stringResource(Res.string.imagepicker_title),
    onDismissRequest: () -> Unit,
    onAction: (PickerAction) -> Unit,
) {
    AppAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        content = {
            Column {
                OptionRow(Icons.Outlined.PhotoCamera, stringResource(Res.string.imagepicker_camera)) {
                    onAction(PickerAction.Camera)
                }
                OptionRow(Icons.Outlined.Image, stringResource(Res.string.imagepicker_gallery)) {
                    onAction(PickerAction.Gallery)
                }
            }
        }
    )
}

@Composable
private fun OptionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppIconPrimary(icon)
        Spacer2()
        TextBodyMediumNeutral100(text = label)
    }
}
