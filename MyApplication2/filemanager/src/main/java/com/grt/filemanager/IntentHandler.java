package com.grt.filemanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.fragment.DataFragment;
import com.grt.filemanager.model.DataAccountInfo;
import com.grt.filemanager.model.DataAccountManger;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.StorageData;
import com.grt.filemanager.model.UsbInfo;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.TimeUtils;

/**
 * Created by LDC on 2018/3/19.
 */

public class IntentHandler {

    private IntentHandler intentHandler;
    private AppCompatActivity activity;
    public void setActivity(AppCompatActivity activity){
        this.activity = activity;
    }
    public void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        int newIntentAction = bundle.getInt(ViewConstant.NEW_INTENT_ACTION, -1);
        switch (newIntentAction) {

            case ViewConstant.ADD_USB_MASS_STORAGE:
                addUsbMassStorage(bundle);
                break;
            case ViewConstant.DISPLAY_DATA:
                Fragment fragment = new DataFragment();
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.data_layout, fragment).commit();
                break;
            case ViewConstant.ADD_EXTERNAL_SDCARD:
                startOpenDocTreeIntent(activity);
                break;
        }

    }
    public static void startOpenDocTreeIntent(Activity activity) {
        if (BuildUtils.thanLollipop()) {
            try {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                activity.startActivityForResult(intent, ActivityResultHandler.EXTERNAL_SDCARD_GRANT_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addUsbMassStorage(Bundle args) {

        int action = args.getInt(UsbInfo.USB_ACTION);
        switch (action) {
//
            case UsbInfo.USB_ACTION_MOUNTED:
//                String path = args.getString(UsbInfo.USB_INFO);
//                if (path != null) {
//                    addUsbFragment(path);
//                }
                DocTreeHelper.startUsbGrantIntent(activity);
                break;
        }
    }

    private void addUsbFragment(String mountPoint) {
        DataAccountInfo account = DataAccountManger.getLocalAccount(mountPoint);
//                LogToFile.e(TAG + "+addUsbFragment", "account rootPath" + account.getRootPath() + "\n"
//        +"accout name" + account.getRootName() + "\n" + "totalsize" + account.getTotalSize());
        if (account != null) {

            FileInfo file = StorageData.createStorageObject(account.getRootPath() + "/" +
                    TimeUtils.getCurrentSecondString());
//            LogToFile.e(TAG, "addUsbFragment getMimeType=" + file.getMimeType() +"\n" + "getName="+ file.getName()+ "\n"
//                    +"getPath ="+file.getPath()+"\n"+"getLastModified="+file.getLastModified()+"\n"+"list size ="+
//                    file.getList(false).size()+"\n"+ "getSize="+file.getSize());
            boolean result = file.create(false) == ResultConstant.SUCCESS && file.delete();
            if (result) {
            } else {
                DocTreeHelper.startUsbGrantIntent(activity);
            }

        }
    }
}
