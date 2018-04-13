package com.grt.filemanager.util;

import android.database.Cursor;
import android.os.Bundle;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.orm.dao.base.DBCore;
import com.grt.filemanager.orm.dao.base.SearchDao;

import java.text.DecimalFormat;

/**
 * Created by LDC on 2018/3/7.
 */

public class StorageUtils {

    public static String getPrettyFileSize(long fileSize) {
        double d = 1024d;
        if (fileSize < 0) {
            return "0 Bytes";
        }

        if (fileSize < 1024) {
            return fileSize + " Bytes";
        }

        DecimalFormat df = new DecimalFormat("######0.00");
        if (fileSize < d * d) {
            return (df.format(fileSize / d)) + " KB";
        }
        if (fileSize < d * d * d) {
            return df.format(fileSize / (d * d)) + " MB";
        }
        return df.format(fileSize / (d * d * d)) + " GB";
    }

    public static Bundle getChildrenLengthAndCount(String path) {
        String[] selectionArgs = new String[]{path + "/%"};

        String sql = "select sum(" + SearchDao.Properties.Size.columnName + ")," + " count(*)" +
                " from " + SearchDao.TABLENAME +
                " where " + SearchDao.Properties.Path.columnName + " like ? ";

        Bundle args = new Bundle();
        Cursor cursor = DBCore.getDaoSession().getDatabase().rawQuery(sql, selectionArgs);
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
}
