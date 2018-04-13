package com.grt.filemanager.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.orm.dao.AppFolder;
import com.grt.filemanager.orm.dao.base.AppFolderDao;
import com.grt.filemanager.orm.helper.DbUtils;

import java.util.List;

public class AppUtils {
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static int appBarHeight = 0;
    private static PackageManager pm = ApiPresenter.getContext().getPackageManager();
    private AppUtils() {
    }

    /**
     * apk 信息
     * storage 和 应用
     *
     * @param context     Context
     * @param packageName 应用包名
     * @param bundle      输出参数
     */
    public static void putInstalledAppInfo(Context context, String packageName, Bundle bundle) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo;
        try {

            packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        bundle.putString(DataConstant.PACKAGE_NAME, packageName);
        bundle.putInt(DataConstant.UID, packageInfo.applicationInfo.uid);
        bundle.putString(DataConstant.VERSION_NAME, packageInfo.versionName);

        String[] signArray = new String[packageInfo.signatures.length];
        Signature[] signatures = packageInfo.signatures;
        int count = signArray.length;
        for (int index = 0; index < count; index++) {
            if (index == 0) {
                String sign = SecurityUtils.hexdigest(signatures[0].toByteArray());
                bundle.putString(DataConstant.SignatureInfo, sign);
            } else {
                signArray[index] = signatures[index].toCharsString();
            }
        }

        bundle.putString(DataConstant.SOURCE_PATH, packageInfo.applicationInfo.dataDir);
        bundle.putString(DataConstant.INSTALL_TIME,
                TimeUtils.getDateString(packageInfo.firstInstallTime, TimeUtils.DATE_FORMAT1));
        bundle.putString(DataConstant.UPDATE_TIME,
                TimeUtils.getDateString(packageInfo.lastUpdateTime, TimeUtils.DATE_FORMAT1));
        bundle.putInt(DataConstant.TARGET_SDK_VERSION,
                packageInfo.applicationInfo.targetSdkVersion);
    }

    public static Drawable getAppDrawable(String name) {

        int tag = PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA;
        PackageInfo apkPackage = null;

        List<AppFolder> appFolderList = DbUtils.getAppFolderHelper().queryBuilder()
                .where(AppFolderDao.Properties.Folder.eq(name)).list();
        if (appFolderList != null) {
            for (AppFolder appFolder : appFolderList) {
                try {
                    apkPackage = pm.getPackageInfo(appFolder.getPackageName(), tag);
                } catch (Exception e) {
                }

                if (apkPackage != null) {
                    break;
                }
            }
        }

        if (apkPackage != null) {
            ApplicationInfo apk = apkPackage.applicationInfo;
            return apk.loadIcon(pm);
        }

        return null;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenHeight = dm.heightPixels;
        }

        return screenHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        if (context != null) {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static void setAppBarHeight(int height) {
        appBarHeight = height;
    }

    public static int getAppBarHeight() {
        return appBarHeight;
    }

}
