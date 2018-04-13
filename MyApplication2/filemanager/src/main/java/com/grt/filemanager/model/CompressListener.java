package com.grt.filemanager.model;
public interface CompressListener {

    /**
     * 压缩一个文件之前的回调
     *
     * @param path 压缩的当前文件路径
     */
    void startCompressCallback(String path);

    void onProgress(long count) throws InterruptedException;

    /**
     * 压缩一个文件完成后回调
     *
     * @param path 压缩的当前文件路径
     */
    void finishCompressCallback(String path);

}
