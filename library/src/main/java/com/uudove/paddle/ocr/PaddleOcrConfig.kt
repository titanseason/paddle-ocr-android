package com.uudove.paddle.ocr

data class PaddleOcrConfig(

    val useOpencl: Int = 0,

    /** CPU线程数  */
    val cpuThreadNum: Int = 4,

    /** CPU能耗  */
    val cpuPowerMode: PaddleCpuPowerMode = PaddleCpuPowerMode.LITE_POWER_HIGH,

    /** 检测模型文件  */
    val detModelPath: String,

    /** 识别模型文件  */
    val recModelPath: String,

    /** 分类模型文件  */
    val clsModelPath: String,

    /** 标签路径  */
    val labelPath: String,

    /** 打印日志  */
    val logger: OcrLogger = OcrLoggerDefaultImpl(),
    /** 输入图像格式，当前仅支持BGR、RGB  */
    val inputColorFormat: String = "BGR",

    /** 输入图像大小。数组大小必须是3。第1位是batch大小；第2位是channel数，当前仅支持1或者3；第3位是图片长边的最大值，如果超过会缩放到该值 */
    val inputShape: IntArray = intArrayOf(1, 3, 1280),

    /** 数组的大小，必须跟channel数一致 */
    val inputMean: FloatArray = floatArrayOf(0.485F, 0.456F, 0.406F),

    /** 数组的大小，必须跟channel数一致 */
    val inputStd: FloatArray = floatArrayOf(0.229F, 0.224F, 0.225F),

    val scoreThreshold: Float = 0.1F,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PaddleOcrConfig

        if (useOpencl != other.useOpencl) return false
        if (cpuThreadNum != other.cpuThreadNum) return false
        if (cpuPowerMode != other.cpuPowerMode) return false
        if (detModelPath != other.detModelPath) return false
        if (recModelPath != other.recModelPath) return false
        if (clsModelPath != other.clsModelPath) return false
        if (labelPath != other.labelPath) return false
        if (logger != other.logger) return false
        if (inputColorFormat != other.inputColorFormat) return false
        if (!inputShape.contentEquals(other.inputShape)) return false
        if (!inputMean.contentEquals(other.inputMean)) return false
        if (!inputStd.contentEquals(other.inputStd)) return false
        return scoreThreshold == other.scoreThreshold
    }

    override fun hashCode(): Int {
        var result = useOpencl
        result = 31 * result + cpuThreadNum
        result = 31 * result + cpuPowerMode.hashCode()
        result = 31 * result + detModelPath.hashCode()
        result = 31 * result + recModelPath.hashCode()
        result = 31 * result + clsModelPath.hashCode()
        result = 31 * result + labelPath.hashCode()
        result = 31 * result + logger.hashCode()
        result = 31 * result + inputColorFormat.hashCode()
        result = 31 * result + inputShape.contentHashCode()
        result = 31 * result + inputMean.contentHashCode()
        result = 31 * result + inputStd.contentHashCode()
        result = 31 * result + scoreThreshold.hashCode()
        return result
    }
}
