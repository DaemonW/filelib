package com.grt.filemanager.adapter;

import android.view.View;

import com.grt.filemanager.fragment.DataFragment;

/**
 * Created by LDC on 2018/3/14.
 */

public class StorageItemListener extends BaseItemListener {
    public StorageItemListener(int dataId, int accountId, long fragmentId, DataFragment fragment) {
        super(dataId, accountId, fragmentId, fragment);
    }
}
