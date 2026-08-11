package sk.mkdigital.kmpshowcase.presentation.foundation

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateMeasurement
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.offset
import kotlinx.cinterop.useContents
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSValue
import platform.UIKit.CGRectValue
import platform.UIKit.UIKeyboardFrameEndUserInfoKey
import platform.UIKit.UIKeyboardWillHideNotification
import platform.UIKit.UIKeyboardWillShowNotification
import platform.darwin.NSObjectProtocol
import kotlin.math.roundToInt

actual fun Modifier.keyboardPadding(): Modifier = this then KeyboardPaddingElement

private object KeyboardPaddingElement : ModifierNodeElement<KeyboardPaddingNode>() {
    override fun create() = KeyboardPaddingNode()

    override fun update(node: KeyboardPaddingNode) = Unit

    override fun equals(other: Any?) = other === this

    override fun hashCode() = "keyboardPadding".hashCode()
}

private class KeyboardPaddingNode : Modifier.Node(), LayoutModifierNode {

    private var keyboardHeight = 0f
        set(value) {
            if (field != value) {
                field = value
                if (isAttached) invalidateMeasurement()
            }
        }

    private var showObserver: NSObjectProtocol? = null
    private var hideObserver: NSObjectProtocol? = null

    override fun onAttach() {
        val center = NSNotificationCenter.defaultCenter

        showObserver = center.addObserverForName(
            name = UIKeyboardWillShowNotification,
            `object` = null,
            queue = null
        ) { notification: NSNotification? ->
            notification?.userInfo?.let { userInfo ->
                val keyboardFrame = userInfo[UIKeyboardFrameEndUserInfoKey] as? NSValue
                keyboardFrame?.let { nsValue ->
                    val rect = nsValue.CGRectValue()
                    keyboardHeight = rect.useContents { size.height }.toFloat()
                }
            }
        }

        hideObserver = center.addObserverForName(
            name = UIKeyboardWillHideNotification,
            `object` = null,
            queue = null
        ) { _: NSNotification? ->
            keyboardHeight = 0f
        }
    }

    override fun onDetach() {
        val center = NSNotificationCenter.defaultCenter
        showObserver?.let { center.removeObserver(it) }
        hideObserver?.let { center.removeObserver(it) }
        showObserver = null
        hideObserver = null
    }

    override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints): MeasureResult {
        val bottom = keyboardHeight.roundToInt()
        val placeable = measurable.measure(constraints.offset(vertical = -bottom))
        return layout(
            width = constraints.constrainWidth(placeable.width),
            height = constraints.constrainHeight(placeable.height + bottom)
        ) {
            placeable.place(0, 0)
        }
    }
}
