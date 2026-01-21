package mk.digital.kmpshowcase.presentation.base.router

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.net.toUri

actual class LinkRouterImpl(private val context: Context) : LinkRouter {
    actual override fun openLink(url: String) {
        if (url.isNotBlank() && url.startsWith("http")) {

            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } else {
            Log.e("LinkRouter", "Invalid URL: '$url'")
        }
    }
}
