package mk.digital.kmpshowcase.presentation.component.image

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import mk.digital.kmpshowcase.presentation.foundation.iconSize


@Composable
fun AppIcon(
    imageVector: ImageVector,
    size: Dp = iconSize,
    tint: Color = LocalContentColor.current,
    contentDescription: String = imageVector.name,
) {
    Icon(
        modifier = Modifier.size(size),
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}

@Composable
fun AppIconPrimary(
    imageVector: ImageVector,
    size: Dp = iconSize,
    contentDescription: String = imageVector.name,
) {
    AppIcon(
        size = size,
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.primary,
    )
}
