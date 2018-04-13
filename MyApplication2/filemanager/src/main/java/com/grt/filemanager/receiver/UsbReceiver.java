package com.grt.filemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.model.DataAccountInfo;
import com.grt.filemanager.model.DataAccountManger;
import com.grt.filemanager.model.UsbInfo;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.LogUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;


public class UsbReceiver extends BroadcastReceiver {

    private static UsbReceiver receiver;
    private String TAG = "UsbReceiver";

    public static UsbReceiver getReceiver() {
        if (receiver == null) {
            receiver = new UsbReceiver();
        }
        return receiver;
    }

    public static void registerReceiver(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }

        if (!UsbInfo.isRegister()) {
            UsbInfo.setRegister(true);
            context.getApplicationContext().registerReceiver(UsbReceiver.getReceiver(),
                    new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (device != null) {
                removeUsbDevice();
            }
        }
    }

    private void removeUsbDevice() {
        Observable.just(StorageManagerUtil.getInstant().getUsbPath())
                .map(new Func1<String, DataAccountInfo>() {
                    @Override
                    public DataAccountInfo call(String path) {
                        LogUtils.e(TAG + "removeUsbDevice", "removeUsbDevice path = " + path);
                        return DataAccountManger.getAccountInfo(DataConstant.STORAGE_DATA_ID, path);
                    }
                })
                .filter(new Func1<DataAccountInfo, Boolean>() {
                    @Override
                    public Boolean call(DataAccountInfo dataAccountInfo) {
                        return dataAccountInfo != null;
                    }
                })
                .map(new Func1<DataAccountInfo, Boolean>() {
                    @Override
                    public Boolean call(DataAccountInfo account) {
//                        EventBusHelper.showUsbRemoveDlg(StorageManagerUtil.getInstant().getUsbPath());

                        DataAccountInfo memoryAccount = DataAccountManger.getMemoryAccount();
                        if (memoryAccount != null && account.getAccountId() > 0) {
//                            EventBusHelper.reInitFragment(account.getDataId(), account.getAccountId(),
//                                    memoryAccount.getDataId(), memoryAccount.getAccountId(), memoryAccount.getRootName(),
//                                    FileUtils.getParentPath(StorageManagerUtil.getInstant().getUsbPath()));
                        } else {
//                            EventBusHelper.refreshSpecialAccount(account.getDataId(), account.getAccountId());
                        }

                        StorageManagerUtil.getInstant().setUsbPath(null);
                        if (BuildUtils.lessMarshmallow()) {
                            UsbInfo.setUsbPath(null);
                        }
                        return true;
                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
//                        EventBusHelper.updateLeftDrawerGroup(DataConstant.STORAGE_DATA_ID);
                        Toast.makeText(ApiPresenter.getContext(),"USB拔掉", Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

}
