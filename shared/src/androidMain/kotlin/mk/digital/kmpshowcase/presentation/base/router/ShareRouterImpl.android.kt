package mk.digital.kmpshowcase.presentation.base.router

import android.content.Context
import android.content.Intent

actual class ShareRouterImpl(private val context: Context) : ShareRouter {
    actual override fun share(
        text: String
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(sendIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

