package com.grt.filemanager.model;

import android.os.Bundle;

import java.util.List;

/**
 * 压缩文件信息接口
 */
public interface ArchiveFileInfo extends FileInfo {

    /**
     * 压缩选中文件路径列表
     *
     * @param pathList 选中文件路径列表
     * @return 压缩操作结果
     */
    int compressZipFiles(List<String> pathList, String zipFilePath, String desFolderPath,
                         String password, Bundle args, CompressListener listener)
            throws InterruptedException;

    /**
     * 解压缩整个压缩文件到指定目录
     *
     * @param desFolder 解压到的目的目录
     * @param password  压缩文件密码
     * @param charset   解压文件编码格式
     * @param args      解压时配置参数
     * @return 返回解压操作的结果
     */
    int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                           ExtractListener listener) throws InterruptedException;

    /**
     * 解压缩压缩文件中选定内容到指定目录
     *
     * @param desFolder 解压到的目的目录
     * @param password  压缩文件密码
     * @param charset   解压文件编码格式
     * @param args      解压时配置参数
     * @return 返回解压操作的结果
     */
    int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                          String charset, Bundle args, ExtractListener listener)
            throws InterruptedException;

    /**
     * 解压缩当前文件到指定目录
     *
     * @param desFolder 解压到的目的目录
     * @param password  压缩文件密码
     * @param charset   解压文件编码格式
     * @param args      解压时配置参数
     * @return 解压成功则返回解压文件的路径，失败则返回null
     */
    String extractFile(String desFolder, String password, String charset, Bundle args);

    /**
     * 当前压缩文件是否加密
     *
     * @return 压缩文件加密返回true，反之则返回false
     */
    boolean isEncrypted();

    /**
     * 当前文件信息接口代表预览压缩文件中的文件
     */
    boolean isHeader();

}
