package com.uudove.paddle.ocr

import android.graphics.Point

data class PaddleOcrResult(
    var points: MutableList<Point> = mutableListOf(),
    var wordIndex: MutableList<Int> = mutableListOf(),
    var label: String = "",
    var confidence: Float = 0F,
) {
    fun addPoints(x: Int, y: Int) {
        val point = Point(x, y)
        points.add(point)
    }

    fun addWordIndex(index: Int) {
        wordIndex.add(index)
    }
}