package com.grt.filemanager.model;

/**
 * Created by LDC on 2018/3/8.
 */

public class UsbData {
    public int dataId;
    public int accountId;
    public int accountName;

    public UsbData(int dataId, int accountId, int accountName){
        this.dataId = dataId;
        this.accountId = accountId;
        this.accountName = accountName;
    }
}
