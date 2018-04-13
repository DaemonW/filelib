package com.grt.filemanager.model;

import android.os.Bundle;

import java.util.List;

public interface DataModel {

    /**
     * 返回当前数据源的账户ID
     *
     * @return 账户ID
     */
    int getAccountId();

    /**
     * 设置当前数据源的根路径
     *
     * @param rootPath 根路径
     */
    void setRootPath(String rootPath);

    /**
     * @return 当前目录的文件信息接口
     */
    FileInfo getCurrentFolder();

    /**
     * 强行设置当前目录文件
     *
     * @param fileInfo 文件信息接口
     */
    void setCurrentFileInfo(FileInfo fileInfo);

    /**
     * 获得指定位置的文件信息接口
     *
     * @param position 用户点击位置
     * @return 文件信息接口
     */
    FileInfo getCurrentChildren(int position);

    /**
     * @return 返回当前目录所有子文件个数
     */
    int childrenCount();

    void setHideFileArg(boolean show);

    void setCurrentPath(int type, String path, int position, int sortType, int sortOrder);

    /**
     * 获得当前数据源根目录路径
     *
     * @return 当前数据源根目录路径
     */
    String getRootPath();

    /**
     * @return 路径栈
     */
    PathStack getPathStack();

    /**
     * 恢复路径栈
     *
     * @param stack 路径栈字符串
     */
    void setPathStack(String stack);

    /**
     * 根据路径获得文件信息
     *
     * @param path 文件路径
     * @return 文件信息
     */
    FileInfo getFileInfo(String path);

    /**
     * 添加一条item信息
     *
     * @param path 添加的子文件的路径
     */
    void addChild(String path, int position);

    /**
     * 添加一条item信息
     *
     * @param file     文件信息接口
     * @param position 新增文件位置
     */
    void addChild(FileInfo file, int position);

    /**
     * 清除当前目录中指定位置在内存中保存的信息
     *
     * @param position 位置
     */
    void deleteChild(int position);

    /**
     * 添加缓存文件信息
     *
     * @param file     新增文件信息接口
     * @param srcPath  原路径
     * @param finished
     */
    void addDbCache(FileInfo file, String srcPath, boolean finished);

    /**
     * 启动一个服务，批量的更新新增文件的系统信息，与stopDBCacheService方法配合使用
     *
     * @param file 新增文件的信息接口
     */
    void addDbCache(FileInfo file);

    /**
     * 停止一个更新新增文件系统信息的服务
     */
    void stopDBCacheService();

    /**
     * 删除磁盘缓存信息
     *
     * @param path     文件路径
     * @param isFolder 是否为文件夹
     */
    void deleteDbCache(String path, boolean isFolder);

    /**
     * 更新磁盘缓存信息
     *
     * @param oldPath 原文件路径
     * @param file    修改后文件信息
     */
    void updateDbCache(final String oldPath, final FileInfo file);

    /**
     * 清除所有磁盘缓存
     */
    void clearDbCache();

    /**
     * 计算文件夹中所有子文件大小和个数
     *
     * @param path          文件路径或者文件ID
     * @param includeFolder 计算时是否包含文件夹
     * @return 返回指定目录中所有子文件的大小和个数
     */
    Bundle calcChildrenSizeAndCounts(String path, boolean includeFolder);

    /**
     * 获得当前目录的子文件列表
     *
     * @return 子文件列表
     */
    List<FileInfo> getChildren();

    /**
     * 筛选出指定文件类型的子文件列表
     *
     * @param type 文件类型--分为两种：文件和文件夹
     */
    void filtrationChildren(int type);

    /**
     * 执行查询任务
     *
     * @param searchDataId 查询依赖条件，对应的数据来源
     * @param keyword      查询关键字
     */
    List<FileInfo> doSearch(int searchDataId, String... keyword);

}
