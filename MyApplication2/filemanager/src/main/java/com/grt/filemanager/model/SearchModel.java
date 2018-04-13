package com.grt.filemanager.model;

/**
 * Created by LDC on 2018/3/16.
 */

public interface SearchModel extends ArchiveModel {
    /**
     * 执行查询任务
     *
     * @param sortType     排序类型
     * @param sortOrder    排序顺序
     * @param searchDataId 查询依赖条件，对应的数据来源
     * @param keyword      查询关键字
     */
    void doSearch(int sortType, int sortOrder, int searchDataId, String... keyword);
}
