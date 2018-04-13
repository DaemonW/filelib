package com.grt.filemanager.model;

public interface ArchiveModel extends DataModel {

    /**
     * 获取一个压缩文件的信息接口
     *
     * @param path      文件路径
     * @param password  压缩密码
     * @param createNew 是否从缓存中获取文件信息接口
     * @return 文件信息接口
     */
    ArchiveFileInfo getFileInfo(String path, String password, boolean createNew);

    /**
     * 添加缓存文件信息
     *
     * @param file 新增文件信息接口
     */
    void addDbCacheForArchive(FileInfo file);
}
