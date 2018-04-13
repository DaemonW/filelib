package com.grt.filemanager.model;

import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.util.LogToFile;

public class DataFactory {

    private DataFactory() {
    }

    /**
     * 获取已存在的Fragment界面对应的操作数据的接口
     *
     * @param fragmentId 对应Fragment界面ID
     * @return 操作数据的接口
     */
    public static DataModel getData(long fragmentId) {
        return BaseData.getDataClassMap().get(fragmentId);
    }

    public static void removeData(long fragmentId) {
        BaseData.getDataClassMap().remove(fragmentId);
    }

    /**
     * 操作数据的接口，不存在对应的Fragment界面
     *
     * @param dataId 数据源ID
     * @return 操作数据的接口
     */
    public static DataModel createData(int dataId) {
        return createData(dataId, 0);
    }

    /**
     * 为Fragment界面获取操作数据的接口
     *
     * @param dataId     数据源ID
     * @param accountId  账户ID
     * @param fragmentId 对应Fragment界面ID
     * @return 操作数据的接口
     */
    public static DataModel createData(int dataId, int accountId, long fragmentId) {

        ArrayMap<Long, BaseData> dataClassMap = BaseData.getDataClassMap();
        BaseData baseData = dataClassMap.get(fragmentId);
//        LogToFile.e("DataFactory", "baseData = "+(baseData == null)+"///accountid ="+accountId);
        if (baseData == null) {
            baseData = createData(dataId, accountId);
            dataClassMap.put(fragmentId, baseData);
        }

        return dataClassMap.get(fragmentId);
    }

    public static BaseData createData(int dataId, int accountId) {
        BaseData baseData;

        switch (dataId) {
            case DataConstant.STORAGE_DATA_ID:
                baseData = new StorageData(accountId);
                break;

            default:
                baseData = new StorageData(accountId);
                break;
        }

        return baseData;
    }

    public static FileInfo createLocalObject(String path) {
        return StorageData.createStorageObject(path);
    }

}
