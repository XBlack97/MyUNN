package com.x.myunn.model

import android.graphics.Bitmap
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import java.io.File

/**
 * An image conists of a [Bitmap] and a filename.
 */
data class Image(var uri: String?, var bitmap: RoundedBitmapDrawable?, var file: File?)