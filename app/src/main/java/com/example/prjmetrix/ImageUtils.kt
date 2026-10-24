package com.example.prjmetrix


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.File

object ImageUtils {

    // Decodes an image
    fun decodeScaled(context: Context, uri: Uri, maxSide: Int): Bitmap? = runCatching {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val w = info.size.width
            val h = info.size.height
            val longest = maxOf(w, h)
            if (longest > maxSide) {
                val scale = maxSide.toFloat() / longest
                decoder.setTargetSize(
                    (w * scale).toInt().coerceAtLeast(1),
                    (h * scale).toInt().coerceAtLeast(1)
                )
            }
        }
    }.getOrNull()

    fun toScaledJpegFile(context: Context, uri: Uri, maxSide: Int = 1600): File? {
        val bitmap = decodeScaled(context, uri, maxSide) ?: return null
        val file = File(context.cacheDir, "upload_${System.nanoTime()}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        return file
    }
}
