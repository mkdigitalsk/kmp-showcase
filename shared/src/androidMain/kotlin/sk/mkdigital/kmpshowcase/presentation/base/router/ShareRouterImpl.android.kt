package sk.mkdigital.kmpshowcase.presentation.base.router

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import sk.mkdigital.kmpshowcase.shared.R
import java.io.File

actual class ShareRouterImpl(private val context: Context) : ShareRouter {
    actual override fun share(
        text: String,
        title: String,
        url: String
    ) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$text\n\n$url")
            putExtra(Intent.EXTRA_TITLE, title)
            // The thumbnail rides the ClipData and appears only when a title is set.
            previewUri()?.let { uri ->
                clipData = ClipData.newUri(context.contentResolver, title, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(sendIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun previewUri(): Uri? = runCatching {
        val file = File(context.cacheDir, "$SHARE_DIR/$PREVIEW_FILE")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            context.resources.openRawResource(R.drawable.share_preview).use { input ->
                file.outputStream().use(input::copyTo)
            }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }.getOrNull()

    private companion object {
        private const val SHARE_DIR = "share"
        private const val PREVIEW_FILE = "preview.png"
    }
}
