package com.grt.filemanager.adapter;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.fragment.DataFragment;

/**
 * Created by LDC on 2018/3/14.
 */

public class ItemListenerFactory {

    public static BaseItemListener createItemListener(int dataId, int accountId, long fragmentId, DataFragment fragment) {
        BaseItemListener itemListener;

        switch (dataId) {
            case DataConstant.STORAGE_DATA_ID:
                itemListener = new StorageItemListener(dataId, accountId, fragmentId, fragment);
                break;
                default:
                    itemListener = new StorageItemListener(dataId, accountId, fragmentId, fragment);
        }

        return itemListener;
    }
}
