package mk.digital.kmpshowcase.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import mk.digital.kmpshowcase.presentation.component.buttons.AppTextButton
import mk.digital.kmpshowcase.presentation.component.buttons.ContainedButton
import mk.digital.kmpshowcase.presentation.component.buttons.OutlinedButton
import mk.digital.kmpshowcase.presentation.component.cards.AppElevatedCard
import mk.digital.kmpshowcase.presentation.component.dividers.AppDividerPrimary
import mk.digital.kmpshowcase.presentation.component.image.AppImage
import mk.digital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import mk.digital.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import mk.digital.kmpshowcase.presentation.component.text.labelLarge.TextButtonPrimary
import mk.digital.kmpshowcase.presentation.foundation.AppTheme
import mk.digital.kmpshowcase.presentation.foundation.space4

@Composable
fun ShowCase() {
    AppTheme {
        Column {
            TopAppBar(title = "TopAppBar")
            Spacer4()
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(space4)
            ) {
                Spacer4()
                AppDividerPrimary()
                Spacer4()
                AppTextButton("Text Button", onClick = {})
                Spacer4()
                ContainedButton(text = "Contained Button", onClick = {})
                Spacer4()
                OutlinedButton(text = "Outlined Button", onClick = {})
                Spacer4()
                val showDialog = remember { mutableStateOf(false) }
                AppElevatedCard(
                    modifier = Modifier.fillMaxWidth().clickable {
                        showDialog.value = !showDialog.value
                    }.padding(space4),
                ) {
                    TextHeadlineMediumPrimary("Text H4 Primary")
                    Spacer4()
                    TextButtonPrimary("App card - Text Button Primary")
                }
                Spacer4()
                AppImage(
                    imageVector = Icons.AutoMirrored.Filled.AddToHomeScreen,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer4()
                CircularProgress()
                Spacer4()
                val checkedCheckBox = remember { mutableStateOf(false) }
                AppCheckbox(
                    checked = checkedCheckBox.value,
                    onCheckedChange = { checkedCheckBox.value = !checkedCheckBox.value })
                Spacer4()

                if (showDialog.value) {
                    AppConfirmDialog(
                        onDismissRequest = {
                            showDialog.value = false
                        },
                        title = "Title",
                        text = "body"
                    )
                }
            }
        }
    }
}
