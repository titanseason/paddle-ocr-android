package com.uudove.paddle.ocr

import android.graphics.Bitmap

import kotlin.math.floor
import kotlin.math.max


internal object ImageTransformer {
    fun resizeWithStep(bitmap: Bitmap, maxLength: Int, step: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val maxWH = max(width, height)
        var newWidth = width
        var newHeight = height
        if (maxWH > maxLength) {
            val ratio = maxLength * 1.0f / maxWH
            newWidth = floor((ratio * width).toDouble()).toInt()
            newHeight = floor((ratio * height).toDouble()).toInt()
        }
        newWidth -= newWidth % step
        if (newWidth == 0) {
            newWidth = step
        }
        newHeight -= newHeight % step
        if (newHeight == 0) {
            newHeight = step
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

}