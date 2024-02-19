package com.uudove.paddle.ocr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.graphics.Bitmap;

class PaddleOcrJni {

    /** C++指针 */
    private long nativePointer = 0;

    /** 加载so（静态方法，仅需加载一次） */
    private static final AtomicBoolean isSOLoaded = new AtomicBoolean();

    public PaddleOcrJni() {
        loadLibrary();
    }

    private void loadLibrary() {
        if (!isSOLoaded.get() && isSOLoaded.compareAndSet(false, true)) {
            try {
                System.loadLibrary("paddle_ocr_jni");
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public boolean init(PaddleOcrConfig config) {
        // 加载so
        if (!isSOLoaded.get()) {
            loadLibrary();
            if (!isSOLoaded.get()) {
                return false;
            }
        }
        // 加载模型
        nativePointer = nativeInit(config.getDetModelPath(),
                config.getRecModelPath(),
                config.getClsModelPath(),
                config.getUseOpencl(),
                config.getCpuThreadNum(),
                config.getCpuPowerMode().getValue());
        return nativePointer != 0;
    }

    public void destroy() {
        if (nativePointer != 0) {
            nativeRelease(nativePointer);
            nativePointer = 0;
        }
    }

    public List<PaddleOcrResult> runImage(float[] inputData,
                                          int width,
                                          int height,
                                          int channels,
                                          Bitmap originalImage) {
        float[] dims = new float[] {1, channels, height, width};
        float[] rawResults = nativeForward(nativePointer, inputData, dims, originalImage);
        return postProcess(rawResults);
    }

    private List<PaddleOcrResult> postProcess(float[] raw) {
        List<PaddleOcrResult> results = new ArrayList<>();
        int begin = 0;

        while (begin < raw.length) {
            int point_num = Math.round(raw[begin]);
            int word_num = Math.round(raw[begin + 1]);
            PaddleOcrResult model = parse(raw, begin + 2, point_num, word_num);
            begin += 2 + 1 + point_num * 2 + word_num;
            results.add(model);
        }

        return results;
    }

    private PaddleOcrResult parse(float[] raw, int begin, int pointNum, int wordNum) {
        int current = begin;
        PaddleOcrResult model = new PaddleOcrResult();
        model.setConfidence(raw[current]);
        current++;
        for (int i = 0; i < pointNum; i++) {
            model.addPoints(Math.round(raw[current + i * 2]), Math.round(raw[current + i * 2 + 1]));
        }
        current += (pointNum * 2);
        for (int i = 0; i < wordNum; i++) {
            int index = Math.round(raw[current + i]);
            model.addWordIndex(index);
        }
        return model;
    }

    private native long nativeInit(String detModelPath,
                                   String recModelPath,
                                   String clsModelPath,
                                   int useOpencl,
                                   int threadNum,
                                   String cpuMode);

    private native float[] nativeForward(long pointer,
                                         float[] buf,
                                         float[] dims,
                                         Bitmap originalImage);

    private native void nativeRelease(long pointer);

}
