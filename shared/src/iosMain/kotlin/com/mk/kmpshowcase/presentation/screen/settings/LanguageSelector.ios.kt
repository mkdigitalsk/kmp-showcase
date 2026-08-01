package com.mk.kmpshowcase.presentation.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargePrimary
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.settings_language
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun LanguageSelector(
    currentLanguage: LanguageState,
    onNavigate: (SettingNavEvents) -> Unit,
) {
    AppElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onNavigate(SettingNavEvents.ToSettings) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(space4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space4)
        ) {
            Image(
                imageVector = currentLanguage.icon,
                contentDescription = stringResource(Res.string.settings_language),
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                TextBodyLargePrimary(stringResource(Res.string.settings_language))
                Spacer2()
                TextBodyMediumNeutral80(stringResource(currentLanguage.stringRes))
            }
        }
    }
}
