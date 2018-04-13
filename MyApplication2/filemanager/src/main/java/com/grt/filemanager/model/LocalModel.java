package com.grt.filemanager.model;

public interface LocalModel extends ArchiveModel {

    /**
     * 递归更新文件夹大小及子文件个数
     */
    void recursionUpdateFolder(String path);

    /**
     * 更新所有父文件夹大小及子文件个数
     */
    void updateAllParent(String path, long addSize, int addCount);

    /**
     * 删除当前文件路径所有父文件夹信息
     *
     * @param path 当前文件路径
     */
    void deleteAllParentCache(String path, long size, int count);

    /**
     * @return 当前数据源是否是可插拔的
     */
    boolean isPortableStorage();

}
