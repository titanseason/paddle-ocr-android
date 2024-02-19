package com.uudove.paddle.ocr

import android.util.Log

internal class OcrLoggerDefaultImpl : OcrLogger {
    override fun log(tag: String, msg: String) {
        Log.i(tag, msg)
    }
}