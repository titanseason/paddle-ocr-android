package com.uudove.paddle.ocr

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.annotation.WorkerThread
import java.io.File
import java.util.Vector


private const val TAG = "PaddleOcrPredictor"

class PaddleOcrPredictor {

    /** C++桥  */
    private var paddleOcrJni: PaddleOcrJni? = null

    /** 标签  */
    private val wordLabels = Vector<String>()

    /** 配置 */
    private var config: PaddleOcrConfig? = null

    /**
     * 初始化（请在子线程加载）
     *
     * @param config 模型配置
     */
    @WorkerThread
    fun init(config: PaddleOcrConfig): Boolean {
        this.config = config

        // 创建JNI（先销毁）
        var startTime = System.currentTimeMillis()
        destroy()
        paddleOcrJni = PaddleOcrJni()
        config.logger.log(TAG, "create jni cost:" + (System.currentTimeMillis() - startTime))
        startTime = System.currentTimeMillis()

        // 加载模型
        var isLoaded: Boolean? = paddleOcrJni?.init(config)
        config.logger.log(TAG, "load models cost: " + (System.currentTimeMillis() - startTime))
        startTime = System.currentTimeMillis()
        if (isLoaded != true) {
            config.logger.log(TAG, "load models failed")
            return false
        }

        // 加载标签
        isLoaded = loadLabels(config.labelPath)
        config.logger.log(TAG, "load labels cost: " + (System.currentTimeMillis() - startTime))
        if (isLoaded != true) {
            config.logger.log(TAG, "load labels failed")
        }
        return isLoaded
    }

    /**
     * 释放内存
     */
    @WorkerThread
    fun destroy() {
        paddleOcrJni?.destroy()
        paddleOcrJni = null
    }

    /**
     * 进行识别
     */
    @WorkerThread
    fun ocr(inputImage: Bitmap): List<PaddleOcrResult>? {
        val inputShape = config!!.inputShape
        val inputColorFormat = config!!.inputColorFormat

        // 对图片进行缩放
        val scaleImage: Bitmap =
            ImageTransformer.resizeWithStep(inputImage, inputShape[2], 32)
        val width = scaleImage.width
        val height = scaleImage.height
        // 将图片转为Int数组
        val pixels = IntArray(width * height)
        scaleImage.getPixels(pixels, 0, width, 0, 0, width, height)

        // config配置
        val channels = inputShape[1]
        val inputMean = config!!.inputMean
        val inputStd = config!!.inputStd

        // 输入数据
        val inputData = FloatArray(channels * width * height)
        if (channels == 3) {
            val channelIdx: IntArray = if (inputColorFormat.contentEquals("RGB", true)) {
                intArrayOf(0, 1, 2)
            } else if (inputColorFormat.contentEquals("BGR", true)) {
                intArrayOf(2, 1, 0)
            } else {
                Log.i(
                    TAG,
                    "Unknown color format $inputColorFormat" + ", only RGB and BGR color format is " +
                            "supported!"
                )
                return null
            }
            val channelStride = intArrayOf(width * height, width * height * 2)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val color: Int = pixels[y * width + x]
                    val rgb = floatArrayOf(
                        Color.red(color) / 255.0f, Color.green(color) / 255.0f,
                        Color.blue(color) / 255.0f
                    )
                    inputData[y * width + x] =
                        (rgb[channelIdx[0]] - inputMean[0]) / inputStd[0]
                    inputData[y * width + x + channelStride[0]] =
                        (rgb[channelIdx[1]] - inputMean[1]) / inputStd[1]
                    inputData[y * width + x + channelStride[1]] =
                        (rgb[channelIdx[2]] - inputMean[2]) / inputStd[2]
                }
            }
        } else if (channels == 1) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val color = pixels[y * width + x]
                    val gray =
                        (Color.red(color) + Color.green(color) + Color.blue(color)) / 3.0f / 255.0f
                    inputData[y * width + x] = (gray - inputMean[0]) / inputStd[0]
                }
            }
        } else {
            Log.i(
                TAG,
                "Unsupported channel size $channels, only channel 1 and 3 is supported!"
            )
            return null
        }

        val results: List<PaddleOcrResult>? =
            paddleOcrJni!!.runImage(inputData, width, height, channels, inputImage)

        return if (results == null) null else postProcess(results)
    }

    private fun loadLabels(path: String): Boolean {
        wordLabels.clear()
        // 加载标签
        try {
            val list = File(path).readLines(Charsets.UTF_8)
            wordLabels.addAll(list)
            config?.logger?.log(TAG, "load labels success, size:${wordLabels.size}")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun postProcess(results: List<PaddleOcrResult>): List<PaddleOcrResult> {
        for (r in results) {
            val word = StringBuffer()
            for (index in r.wordIndex) {
                if (index >= 0 && index < wordLabels.size) {
                    word.append(wordLabels[index])
                } else {
                    Log.e(TAG, "Word index is not in label list:$index")
                    word.append("×")
                }
            }
            r.label = word.toString()
        }
        return results
    }
}