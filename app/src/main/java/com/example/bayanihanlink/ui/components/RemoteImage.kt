package com.example.bayanihanlink.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bayanihanlink.ui.theme.AppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// Downloaded images are kept here so scrolling back and forth does not
// download the same picture again.
private val imageCache = mutableMapOf<String, ImageBitmap>()

private fun loadImageFromUrl(url: String): ImageBitmap? {
    synchronized(imageCache) {
        imageCache[url]?.let { return it }
    }

    val bitmap = try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.inputStream.use { stream ->
            BitmapFactory.decodeStream(stream)?.asImageBitmap()
        }
    } catch (_: Exception) {
        null
    }

    if (bitmap != null) {
        synchronized(imageCache) { imageCache[url] = bitmap }
    }
    return bitmap
}

/**
 * Shows a picture that lives on the internet (for example the evidence photo
 * uploaded with a request).
 *
 * It handles the three situations every network image has:
 *  - still loading  -> a small spinner
 *  - failed / empty -> a friendly "Image unavailable" message
 *  - success        -> the actual picture
 */
@Composable
fun RemoteImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    emptyText: String = "No image attached"
) {
    val colors = AppColors
    val result by produceState<ImageLoadResult>(initialValue = ImageLoadResult.Loading, key1 = imageUrl) {
        value = if (imageUrl.isBlank()) {
            ImageLoadResult.Empty
        } else {
            val bitmap = withContext(Dispatchers.IO) { loadImageFromUrl(imageUrl) }
            if (bitmap == null) ImageLoadResult.Failed else ImageLoadResult.Success(bitmap)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(colors.cardGray),
        contentAlignment = Alignment.Center
    ) {
        when (val state = result) {
            is ImageLoadResult.Success -> Image(
                bitmap = state.bitmap,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )

            ImageLoadResult.Loading -> CircularProgressIndicator(
                color = colors.accentBlue,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )

            ImageLoadResult.Empty -> ImageMessage(
                icon = Icons.Default.Image,
                text = emptyText
            )

            ImageLoadResult.Failed -> ImageMessage(
                icon = Icons.Default.BrokenImage,
                text = "Image unavailable"
            )
        }
    }
}

@Composable
private fun ImageMessage(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    val colors = AppColors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.labelGray,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.labelGray
        )
    }
}

private sealed interface ImageLoadResult {
    data object Loading : ImageLoadResult
    data object Empty : ImageLoadResult
    data object Failed : ImageLoadResult
    data class Success(val bitmap: ImageBitmap) : ImageLoadResult
}
