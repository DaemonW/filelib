package com.grt.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.util.PreferenceUtils;

/**
 * Created by LDC on 2018/3/19.
 */

public class ActivityResultHandler {
    private String TAG = "ActivityResultHandler";
    private static Context context;
    public static final int EXTERNAL_SDCARD_GRANT_PERMISSION = 1;
    public static final int USB_PERMISSION_GRANT = 2;
    public static final int SDCARD_PERMISSION = 3;
    private static ActivityResultHandler instance;
//    private ActivityResultHandler(Context context){
//        this.context = context;
//    }

    public static ActivityResultHandler initInstance(){
        if (instance == null){
            instance = new ActivityResultHandler();
        }
        return instance;
    }

    public void initContext(Context context){
        this.context = context;
    }
    public boolean handleResult(int requestCode, int resultCode, Intent data) {
        boolean result = false;
        switch (requestCode){
            case USB_PERMISSION_GRANT:
                result = handleStorageAccessFrameWork(data, SettingConstant.USB_ROOT_URI, false,
                        StorageManagerUtil.getInstant().getSecondaryStorage());
//                LogToFile.e(TAG, "SecondaryStorage = " + StorageManagerUtil.getInstant().getSecondaryStorage());
                break;
            case EXTERNAL_SDCARD_GRANT_PERMISSION:
                result = handleStorageAccessFrameWork(data, SettingConstant.EXTARNAL_SDCARD_LOLLIPOP,
                        true, StorageManagerUtil.getInstant().getSecondaryStorage());
                break;
            case SDCARD_PERMISSION:
//                if (resultCode == Res)
                break;
        }
        return result;
    }

    private boolean handleStorageAccessFrameWork(Intent data, String urlKey, boolean isSecondary, String secondaryPath) {
//        LogToFile.e(TAG, "handleStorageAccessFrameWork ");
        if (data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                String path = treeUri.getPath();
//                LogToFile.e(TAG, "path = "+path);
                if (!TextUtils.isEmpty(path)) {
                    if (!path.equals("/tree/primary:") && path.endsWith(":")) {
//                        LogToFile.e(TAG, "fsdfs = " + (!path.equals("/tree/primary:") && path.endsWith(":")));
                        try {
                            if (BuildUtils.thanKitkat()) {
                                if (checkPermissionDenied(treeUri, isSecondary, secondaryPath)) {
//                                    LogToFile.e(TAG, "checkPermissionDenied = " + checkPermissionDenied(treeUri, isSecondary, secondaryPath));
                                    return false;
                                }

                                int modeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                                context.getContentResolver().takePersistableUriPermission(treeUri, modeFlags);

                                String str = treeUri.toString();
                                String codeStr = str.substring(str.lastIndexOf("/") + 1);
                                str += "/document/" + codeStr;
//                                LogToFile.e("ActivityResultHandler", "str == " + str);
                                PreferenceUtils.setPrefString(urlKey, str);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

            if (PreferenceUtils.getPrefString(urlKey, null) != null) {
                Toast.makeText(context, context.getString(R.string.permission_success), Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return false;
    }

    private boolean checkPermissionDenied(Uri uri, boolean isSecondary, String secondaryPath) {
        String name = uri.getLastPathSegment();
        name = name.endsWith(":") ? name.substring(0, name.lastIndexOf(":")) : name;
        if (isSecondary) {
            if (TextUtils.isEmpty(secondaryPath) || !secondaryPath.contains(name)) {
                String usbPath = StorageManagerUtil.getInstant().getUsbDevicePath();
                if (usbPath != null) {
                    String mountsPoint = StorageManagerUtil.getInstant().getUsbMountPoint(name);
                    return !TextUtils.isEmpty(mountsPoint) || (mountsPoint != null && mountsPoint.equals(secondaryPath));
                } else {
                    return false;
                }
            }
        } else {
            if (TextUtils.isEmpty(secondaryPath)) {
                return false;
            }

            if (secondaryPath.contains(name)) {
                return !StorageManagerUtil.getInstant().checkUsbMountPoint(secondaryPath, name);
            }
        }

        return false;
    }

}
