package com.grt.filemanager.model;

/**
 * 解压缩文件进度监听
 */
public interface ExtractListener {

    void startExtractCallback(String name);

    void onProgress(long count) throws InterruptedException;

    void finishExtractCallback(boolean result, String tmpPath, String archiveName);

}
