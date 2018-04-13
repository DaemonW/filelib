package com.grt.filemanager.model;

import java.util.List;

public interface DataModule {

    /**
     * @return 获取当前数据源所有的账户信息
     */
    List<DataAccountInfo> getAccountList();

}
