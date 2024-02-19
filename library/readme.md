# Paddle部署

## 1. 下载及部署Paddle-Lite库

Paddle-Lite Git仓库：https://github.com/PaddlePaddle/Paddle-Lite

可以直接在此处下载so库：https://www.paddlepaddle.org.cn/lite/develop/quick_start/release_lib.html 

选择以下两个模块下载
| Arch | toolchain | android_stl | with_extra | with_cv |
| ---- | ---- | ---- | ---- | ---- |
| armv7 | clang | c++_static | OFF | ON |
| armv8 | clang | c++_static | OFF | ON | 

两个压缩包，解压缩后
+ 将 `cxx/lib/libpaddle_light_api_shared.so` 复制到 `src/main/jniLibs/armeabi-v7a|arm64-v8a` 目录下。

## 2. 复制PaddleOcr C++库

参照 Demo: https://github.com/PaddlePaddle/PaddleOCR/tree/release/2.7/deploy/android_demo
+ 将 `PaddleOCR/deploy/android_demo/app/src/main/cpp` 目录下的所有文件复制到 `src/main/cpp` 目录下。



## 3. 下载及部署nb模型

直接到该地址下载模型
https://github.com/PaddlePaddle/PaddleOCR/blob/release/2.7/doc/doc_ch/models_list.md

## 3. Android Demo源码



