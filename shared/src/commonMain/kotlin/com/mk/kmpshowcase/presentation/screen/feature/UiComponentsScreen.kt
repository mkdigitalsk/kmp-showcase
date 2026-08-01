package com.mk.kmpshowcase.presentation.screen.feature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import com.mk.kmpshowcase.LocalSnackbarHostState
import com.mk.kmpshowcase.presentation.component.AppAssistChip
import com.mk.kmpshowcase.presentation.component.AppBadge
import com.mk.kmpshowcase.presentation.component.AppBadgedBox
import com.mk.kmpshowcase.presentation.component.AppBottomSheet
import com.mk.kmpshowcase.presentation.component.AppCheckbox
import com.mk.kmpshowcase.presentation.component.AppConfirmDialog
import com.mk.kmpshowcase.presentation.component.AppDotBadgedBox
import com.mk.kmpshowcase.presentation.component.AppFilterChip
import com.mk.kmpshowcase.presentation.component.AppInputChip
import com.mk.kmpshowcase.presentation.component.AppLinearProgress
import com.mk.kmpshowcase.presentation.component.AppRadioButton
import com.mk.kmpshowcase.presentation.component.AppSlider
import com.mk.kmpshowcase.presentation.component.AppSuggestionChip
import com.mk.kmpshowcase.presentation.component.AppSwitch
import com.mk.kmpshowcase.presentation.component.AppTextField
import com.mk.kmpshowcase.presentation.component.CircularProgress
import com.mk.kmpshowcase.presentation.component.SnackbarType
import com.mk.kmpshowcase.presentation.component.buttons.AppFloatingActionButton
import com.mk.kmpshowcase.presentation.component.buttons.AppSegmentedButton
import com.mk.kmpshowcase.presentation.component.buttons.AppTextButton
import com.mk.kmpshowcase.presentation.component.buttons.ContainedButton
import com.mk.kmpshowcase.presentation.component.buttons.OutlinedButton
import com.mk.kmpshowcase.presentation.component.cards.AppCard
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.dividers.AppDividerPrimary
import com.mk.kmpshowcase.presentation.component.image.AppIcon
import com.mk.kmpshowcase.presentation.component.showSnackbar
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargeNeutral80
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space12
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.bottom_sheet_content
import com.mk.kmpshowcase.shared.generated.resources.bottom_sheet_title
import com.mk.kmpshowcase.shared.generated.resources.button_contained
import com.mk.kmpshowcase.shared.generated.resources.button_outlined
import com.mk.kmpshowcase.shared.generated.resources.button_text
import com.mk.kmpshowcase.shared.generated.resources.card_content
import com.mk.kmpshowcase.shared.generated.resources.card_flat_content
import com.mk.kmpshowcase.shared.generated.resources.checkbox_label
import com.mk.kmpshowcase.shared.generated.resources.chip_assist
import com.mk.kmpshowcase.shared.generated.resources.chip_filter
import com.mk.kmpshowcase.shared.generated.resources.chip_input
import com.mk.kmpshowcase.shared.generated.resources.chip_suggestion
import com.mk.kmpshowcase.shared.generated.resources.close
import com.mk.kmpshowcase.shared.generated.resources.dialog_message
import com.mk.kmpshowcase.shared.generated.resources.dialog_title
import com.mk.kmpshowcase.shared.generated.resources.fab_content_description
import com.mk.kmpshowcase.shared.generated.resources.image_description
import com.mk.kmpshowcase.shared.generated.resources.radio_option_1
import com.mk.kmpshowcase.shared.generated.resources.radio_option_2
import com.mk.kmpshowcase.shared.generated.resources.radio_option_3
import com.mk.kmpshowcase.shared.generated.resources.section_badges
import com.mk.kmpshowcase.shared.generated.resources.section_bottom_sheet
import com.mk.kmpshowcase.shared.generated.resources.section_buttons
import com.mk.kmpshowcase.shared.generated.resources.section_cards
import com.mk.kmpshowcase.shared.generated.resources.section_chips
import com.mk.kmpshowcase.shared.generated.resources.section_controls
import com.mk.kmpshowcase.shared.generated.resources.section_dividers
import com.mk.kmpshowcase.shared.generated.resources.section_feedback
import com.mk.kmpshowcase.shared.generated.resources.section_images
import com.mk.kmpshowcase.shared.generated.resources.section_loading
import com.mk.kmpshowcase.shared.generated.resources.section_radio_buttons
import com.mk.kmpshowcase.shared.generated.resources.section_sliders
import com.mk.kmpshowcase.shared.generated.resources.section_snackbar
import com.mk.kmpshowcase.shared.generated.resources.section_switches
import com.mk.kmpshowcase.shared.generated.resources.section_text_fields
import com.mk.kmpshowcase.shared.generated.resources.section_typography
import com.mk.kmpshowcase.shared.generated.resources.segment_day
import com.mk.kmpshowcase.shared.generated.resources.segment_month
import com.mk.kmpshowcase.shared.generated.resources.segment_week
import com.mk.kmpshowcase.shared.generated.resources.show_bottom_sheet
import com.mk.kmpshowcase.shared.generated.resources.show_dialog
import com.mk.kmpshowcase.shared.generated.resources.show_snackbar_default
import com.mk.kmpshowcase.shared.generated.resources.show_snackbar_error
import com.mk.kmpshowcase.shared.generated.resources.show_snackbar_success
import com.mk.kmpshowcase.shared.generated.resources.show_snackbar_warning
import com.mk.kmpshowcase.shared.generated.resources.snackbar_message_default
import com.mk.kmpshowcase.shared.generated.resources.snackbar_message_error
import com.mk.kmpshowcase.shared.generated.resources.snackbar_message_success
import com.mk.kmpshowcase.shared.generated.resources.snackbar_message_warning
import com.mk.kmpshowcase.shared.generated.resources.switch_label
import com.mk.kmpshowcase.shared.generated.resources.text_field_label
import com.mk.kmpshowcase.shared.generated.resources.text_field_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiComponentsScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var checkboxChecked by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf("") }
    var switchChecked by remember { mutableStateOf(false) }
    var selectedRadioOption by remember { mutableStateOf(0) }
    var selectedChips by remember { mutableStateOf(setOf<Int>()) }
    var sliderValue by remember { mutableStateOf(0.5f) }
    var selectedSegment by remember { mutableStateOf(0) }
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val snackbarMessageDefault = stringResource(Res.string.snackbar_message_default)
    val snackbarMessageSuccess = stringResource(Res.string.snackbar_message_success)
    val snackbarMessageError = stringResource(Res.string.snackbar_message_error)
    val snackbarMessageWarning = stringResource(Res.string.snackbar_message_warning)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = space4)
            .padding(top = space4, bottom = floatingNavBarSpace),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        ComponentSection(title = stringResource(Res.string.section_buttons)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContainedButton(
                    text = stringResource(Res.string.button_contained),
                    onClick = {}
                )
                OutlinedButton(
                    text = stringResource(Res.string.button_outlined),
                    onClick = {}
                )
            }
            Spacer2()
            Row(
                horizontalArrangement = Arrangement.spacedBy(space4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextButton(
                    text = stringResource(Res.string.button_text),
                    onClick = {}
                )
                AppFloatingActionButton(onClick = {}) {
                    AppIcon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(Res.string.fab_content_description)
                    )
                }
            }
            Spacer2()
            AppSegmentedButton(
                options = listOf(
                    stringResource(Res.string.segment_day),
                    stringResource(Res.string.segment_week),
                    stringResource(Res.string.segment_month)
                ),
                selectedIndex = selectedSegment,
                onSelectionChanged = { selectedSegment = it },
                modifier = Modifier.fillMaxWidth()
            )

        }

        ComponentSection(title = stringResource(Res.string.section_typography)) {
            TextHeadlineMediumPrimary("Headline Medium")
            Spacer2()
            TextTitleLargeNeutral80("Title Large")
            Spacer2()
            TextBodyLargeNeutral80("Body Large")
            Spacer2()
            TextBodyMediumNeutral80("Body Medium")
        }

        ComponentSection(title = stringResource(Res.string.section_cards)) {
            AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4), onClick = {}) {
                TextBodyMediumNeutral80(stringResource(Res.string.card_content))
            }
            Spacer2()
            AppCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                TextBodyMediumNeutral80(stringResource(Res.string.card_flat_content))
            }
        }

        ComponentSection(title = stringResource(Res.string.section_controls)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppCheckbox(
                    checked = checkboxChecked,
                    onCheckedChange = { checkboxChecked = it }
                )
                TextBodyMediumNeutral80(stringResource(Res.string.checkbox_label))
            }
        }

        ComponentSection(title = stringResource(Res.string.section_text_fields)) {
            AppTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = stringResource(Res.string.text_field_label),
                placeholder = stringResource(Res.string.text_field_placeholder),
                modifier = Modifier.fillMaxWidth()
            )
        }

        ComponentSection(title = stringResource(Res.string.section_switches)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppSwitch(
                    checked = switchChecked,
                    onCheckedChange = { switchChecked = it }
                )
                TextBodyMediumNeutral80(
                    text = stringResource(Res.string.switch_label),
                    modifier = Modifier.padding(start = space4)
                )
            }
        }

        ComponentSection(title = stringResource(Res.string.section_radio_buttons)) {
            val radioOptions = listOf(
                stringResource(Res.string.radio_option_1),
                stringResource(Res.string.radio_option_2),
                stringResource(Res.string.radio_option_3)
            )
            radioOptions.forEachIndexed { index, label ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppRadioButton(
                        selected = selectedRadioOption == index,
                        onClick = { selectedRadioOption = index }
                    )
                    TextBodyMediumNeutral80(label)
                }
            }
        }

        ComponentSection(title = stringResource(Res.string.section_chips)) {
            Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                AppFilterChip(
                    selected = selectedChips.contains(0),
                    onClick = {
                        selectedChips = if (selectedChips.contains(0)) {
                            selectedChips - 0
                        } else {
                            selectedChips + 0
                        }
                    },
                    label = stringResource(Res.string.chip_filter)
                )
                AppAssistChip(
                    onClick = {},
                    label = stringResource(Res.string.chip_assist)
                )
            }
            Spacer2()
            Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                AppInputChip(
                    selected = selectedChips.contains(1),
                    onClick = {
                        selectedChips = if (selectedChips.contains(1)) {
                            selectedChips - 1
                        } else {
                            selectedChips + 1
                        }
                    },
                    label = stringResource(Res.string.chip_input)
                )
                AppSuggestionChip(
                    onClick = {},
                    label = stringResource(Res.string.chip_suggestion)
                )
            }
        }

        ComponentSection(title = stringResource(Res.string.section_sliders)) {
            AppSlider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        ComponentSection(title = stringResource(Res.string.section_dividers)) {
            TextBodyMediumNeutral80("Content above divider")
            Spacer2()
            AppDividerPrimary()
            Spacer2()
            TextBodyMediumNeutral80("Content below divider")
        }

        ComponentSection(title = stringResource(Res.string.section_loading)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space4)
            ) {
                CircularProgress()
                CircularProgress(size = space12)
            }
            Spacer2()
            AppLinearProgress(modifier = Modifier.fillMaxWidth())
        }

        ComponentSection(title = stringResource(Res.string.section_badges)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppBadgedBox(count = 5) {
                    AppIcon(imageVector = Icons.Filled.Add)
                }
                AppBadgedBox(count = 123) {
                    AppIcon(imageVector = Icons.Filled.Add)
                }
                AppDotBadgedBox(showBadge = true) {
                    AppIcon(imageVector = Icons.Filled.Add)
                }
                AppBadge()
                AppBadge(count = 7)
            }
        }

        ComponentSection(title = stringResource(Res.string.section_images)) {
            AsyncImage(
                model = "https://picsum.photos/400/200",
                contentDescription = stringResource(Res.string.image_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        ComponentSection(title = stringResource(Res.string.section_snackbar)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space4),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    text = stringResource(Res.string.show_snackbar_default),
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackbarMessageDefault,
                                type = SnackbarType.Default
                            )
                        }
                    }
                )
                OutlinedButton(
                    text = stringResource(Res.string.show_snackbar_success),
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackbarMessageSuccess,
                                type = SnackbarType.Success
                            )
                        }
                    }
                )
            }
            Spacer2()
            Row(
                horizontalArrangement = Arrangement.spacedBy(space4),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    text = stringResource(Res.string.show_snackbar_error),
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackbarMessageError,
                                type = SnackbarType.Error
                            )
                        }
                    }
                )
                OutlinedButton(
                    text = stringResource(Res.string.show_snackbar_warning),
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = snackbarMessageWarning,
                                type = SnackbarType.Warning
                            )
                        }
                    }
                )
            }
        }

        ComponentSection(title = stringResource(Res.string.section_bottom_sheet)) {
            ContainedButton(
                text = stringResource(Res.string.show_bottom_sheet),
                onClick = { showBottomSheet = true }
            )
        }

        ComponentSection(title = stringResource(Res.string.section_feedback)) {
            ContainedButton(
                text = stringResource(Res.string.show_dialog),
                onClick = { showDialog = true }
            )
        }

        Spacer4()
    }

    if (showDialog) {
        AppConfirmDialog(
            title = stringResource(Res.string.dialog_title),
            text = stringResource(Res.string.dialog_message),
            onDismissRequest = { showDialog = false }
        )
    }

    if (showBottomSheet) {
        AppBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(space4)
            ) {
                TextHeadlineMediumPrimary(stringResource(Res.string.bottom_sheet_title))
                Spacer2()
                TextBodyMediumNeutral80(stringResource(Res.string.bottom_sheet_content))
                Spacer4()
                ContainedButton(
                    text = stringResource(Res.string.close),
                    onClick = { showBottomSheet = false }
                )
                Spacer4()
            }
        }
    }
}

@Composable
private fun ComponentSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextHeadlineMediumPrimary(title)
        Spacer2()
        content()
    }
}
