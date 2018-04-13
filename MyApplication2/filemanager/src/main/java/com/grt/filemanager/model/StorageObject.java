package com.grt.filemanager.model;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.orm.dao.Storage;
import com.grt.filemanager.orm.helper.DbUtils;
import com.grt.filemanager.util.AppUtils;
import com.grt.filemanager.util.FeThumbUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.MediaHelper;
import com.grt.filemanager.util.PreferenceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StorageObject extends LocalObject {

    private static ArrayMap<String, Bundle> dirInfo = DirInfo.getDirInfo();

    private File mFile;

    public StorageObject(String path) {
        this.mFile = new File(path);
        updateCountAndSize();
    }

    public StorageObject(File file) {
        this.mFile = file;
        updateCountAndSize();
    }

    private void updateCountAndSize() {
        if (PreferenceUtils.getPrefBoolean(SettingConstant.SHOW_DIR_INFO, SettingConstant.SHOW_DIR_INFO_DEFAULT_VALUE)) {
            updateAllCountAndSize(mFile);
        }
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getPath() {
        return mFile.getPath();
    }

    @Override
    public long getLastModified() {
        return mFile.lastModified();
    }

    @Override
    public long getSize() {
        if (mFile.isDirectory()) {
            String path = mFile.getPath();
            Bundle info = dirInfo.get(path);
            if (info != null) {
                long size = info.getLong(DataConstant.ALL_CHILDREN_SIZE);
                if (size < 0) {
                    info = getChildrenLengthAndCount(mFile.getPath());
                    size = info.getLong(DataConstant.ALL_CHILDREN_SIZE);
                    int allCount = info.getInt(DataConstant.ALL_CHILDREN_COUNT);

                    DbUtils.getStorageHelper().saveOrUpdate(new Storage(null, path, size, allCount));
                }

                return size;
            } else {
                return 0;
            }
        } else {
            return mFile.length();
        }
    }

    @Override
    public boolean isFolder() {
        return mFile.isDirectory();
    }

    @Override
    public String getMimeType() {
        return mFile.isDirectory() ? DataConstant.MIME_TYPE_FOLDER : FileUtils.getMiMeType(mFile.getName());
    }

    @Override
    public Bitmap getThumb(boolean isList) {
        Bitmap bitmap = FeThumbUtils.getThumbFromDb(context,
                FileUtils.getMiMeType(mFile.getName()), mFile.getPath(), isList);
        if (bitmap == null) {
            bitmap = FeThumbUtils.createThumb(context, FileUtils.getMiMeType(mFile.getName()),
                    mFile.getPath(), isList);
        }

        return bitmap;
    }

    @Override
    public Bundle getSpecialInfo(String type) {
        Bundle bundle = new Bundle();

        switch (type) {
            case DataConstant.PERMISSION:
                bundle.putString(type, mFile.isDirectory() ? "drwxrwx--" : "-rw-rw----");
                break;

            case DataConstant.ALL_CHILDREN_COUNT:
                if (mFile.isDirectory()) {
                    Bundle args = dirInfo.get(mFile.getPath());
                    if (args != null) {
                        bundle.putInt(type, args.getInt(type));
                    } else {
                        bundle.putInt(type, 0);
                    }
                } else {
                    bundle.putInt(type, 0);
                }
                break;

            case DataConstant.OPERATION_PERMISSION:
                bundle.putString(type, DataConstant.ALL_ALLOW_PERMISSION);
                break;

            case DataConstant.INSTALLED_APP:
                PackageManager pm = context.getPackageManager();
                PackageInfo pkgInfo = pm.getPackageArchiveInfo(mFile.getPath(), PackageManager.GET_ACTIVITIES);
                AppUtils.putInstalledAppInfo(context, pkgInfo.packageName, bundle);
                break;

            default:
                MediaHelper.getMediaHelper().getMediaExtendInfo(type, mFile.getPath(), bundle);
                break;
        }

        return bundle;
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> infoList = new ArrayList<>();
        File[] files = mFile.listFiles();
        if (files != null) {
            for (File curFile : files) {
                StorageObject file = new StorageObject(curFile);
                if (!containHide && file.getName().startsWith(".")) {
                    continue;
                }

                infoList.add(file);
            }
        }

        return infoList;
    }

    private Bundle updateAllCountAndSize(File file) {
        if (file.isDirectory()) {
            String path = file.getPath();
            Bundle bundle = dirInfo.get(path);

            if (bundle == null) {
                long allSize;
                int allCount;

                Storage storage = DbUtils.getStorageHelper().getStorage(path);
                if (storage != null) {
                    allSize = storage.getChildrenSize();
                    allCount = storage.getChildrenCount();
                } else {
                    Bundle args = getChildrenLengthAndCount(path);
                    allSize = args.getLong(DataConstant.ALL_CHILDREN_SIZE);
                    allCount = args.getInt(DataConstant.ALL_CHILDREN_COUNT);

                    DbUtils.getStorageHelper().saveOrUpdate(new Storage(null, path, allSize, allCount));
                }

                return putDirInfoCache(path, allSize, allCount);
            } else {
                return bundle;
            }
        } else {
            return null;
        }
    }

    private Bundle putDirInfoCache(String path, long allSize, int allCount) {
        Bundle bundle = new Bundle();
        bundle.putLong(DataConstant.ALL_CHILDREN_SIZE, allSize);
        bundle.putInt(DataConstant.ALL_CHILDREN_COUNT, allCount);

        try {
            dirInfo.put(path, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bundle;
    }

    public Bundle getChildrenLengthAndCount(String path) {
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                + " count(*)"};
        String selection = MediaStore.Files.FileColumns.DATA + " like ?";
        String[] selectionArgs = new String[]{path + "/%"};
        Bundle args = new Bundle();

        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Files.getContentUri("external"),
                        projection, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.moveToFirst();
            args.putLong(DataConstant.ALL_CHILDREN_SIZE, cursor.getLong(0));
            args.putInt(DataConstant.ALL_CHILDREN_COUNT, cursor.getInt(1));
        }

        if (cursor != null) {
            cursor.close();
        }

        return args;
    }

    @Override
    public boolean rename(String newPath) {
        boolean result;

        result = super.rename(newPath);
        if (result) {
            mFile = new File(newPath);
        }

        return result;
    }

    @Override
    protected String getFilePath() {
        return mFile.getAbsolutePath();
    }

}
