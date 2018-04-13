package com.grt.filemanager.orm.helper.implement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.orm.dao.SafeBox;
import com.grt.filemanager.util.DES;
import com.grt.filemanager.util.TmpFolderUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SafeBoxHelper {

    private static final String cur_tab_name = "boxfe";
    private static final String SAFE_KEY = "boxbox";
    private static final String SAFE_EMAIL_KEY = "boxboxe";
    private static final Context context = ApiPresenter.getContext();

    private static SQLiteDatabase myDB = null;
    private static SafeBoxHelper helper = null;

    public static final String CREATE_TABLE_FILE = "create table if not exists "
            + cur_tab_name
            + "("
            + SafeBox.Properties._ID + " integer primary key,"
            + SafeBox.Properties.TITLE + " TEXT COLLATE NOCASE,"
            + SafeBox.Properties.DATA + " TEXT,"
            + SafeBox.Properties.SIZE + " INTEGER,"
            + SafeBox.Properties.MIME_TYPE + " TEXT,"
            + SafeBox.Properties.DATE_MODIFIED + " INTEGER,"
            + SafeBox.Properties.IS_FILE + " INTEGER,"
            + SafeBox.Properties.PARENT + " INTEGER,"
            + SafeBox.Properties.REAL_PATH + " varchar,"
            + SafeBox.Properties.KEY + " varchar"
            + ")";

    public static SafeBoxHelper getHelper() {
        if (helper == null) {
            helper = new SafeBoxHelper();
        }
        return helper;
    }

    public SafeBoxHelper() {
        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (!isTableExist(myDB, cur_tab_name)) {
            createTable();
        }
    }

    public synchronized void getDB() {
        File databasePath = new File(TmpFolderUtils.getSafeBoxDir());// 创建目录

        File f = new File(databasePath + File.separator + cur_tab_name);// 创建文件--数据库

        if (!databasePath.exists()) {
            databasePath.mkdirs();
        }

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            myDB = SQLiteDatabase.openOrCreateDatabase(f, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized int insert(SafeBox safeBox) {

        if (myDB == null || !myDB.isOpen() || safeBox == null) {
            getDB();
        }

        if (myDB == null) {
            return ResultConstant.FAILED;
        }

        long index = myDB.insert(cur_tab_name, null, getContentValuesBySafeBox(safeBox));
        if (index != -1) {
            return ResultConstant.SUCCESS;
        }

        return ResultConstant.FAILED;
    }

    public synchronized int insert(ContentValues contentValues) {

        if (myDB == null || !myDB.isOpen() || contentValues == null) {
            getDB();
        }

        if (myDB == null) {
            return ResultConstant.FAILED;
        }

        long index = myDB.insert(cur_tab_name, null, contentValues);
        if (index != -1) {
            return ResultConstant.SUCCESS;
        }

        return ResultConstant.FAILED;
    }

    private Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return null;
        }

        Cursor c = myDB.query(cur_tab_name, projection, selection, selectionArgs, null, null, sortOrder);
        c.moveToFirst();
        return c;
    }

    public SafeBox queryFileByPath(String path) {

        SafeBox safeBox = null;
        Cursor cursor = query(null, SafeBox.Properties.DATA + "=?", new String[]{path}, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                safeBox = getSafeBoxByCursor(cursor);
            }
            cursor.close();
        }

        return safeBox;
    }

    public List<SafeBox> queryFilesByParentPath(int parentPathHash) {
        List<SafeBox> safeBoxList = new ArrayList<>();

        Cursor cursor = query(null, SafeBox.Properties.PARENT + "=?", new String[]{parentPathHash + ""}, null);
        if (cursor != null) {

            if (cursor.getCount() > 0) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);

                    safeBoxList.add(getSafeBoxByCursor(cursor));
                }
            }
            cursor.close();
        }

        return safeBoxList;
    }

    public int updateItem(SafeBox safeBox) {
        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (safeBox == null){
            return ResultConstant.FAILED;
        }

        if (myDB == null) {
            return ResultConstant.FAILED;
        }

        int r = myDB.update(cur_tab_name, getContentValuesBySafeBox(safeBox),
                SafeBox.Properties.REAL_PATH + "=?", new String[]{safeBox.getRealPath()});

        if (r != -1) {
            return ResultConstant.SUCCESS;
        }
        return ResultConstant.FAILED;
    }

    public int updateKeys(ContentValues contentValues, String oldKey) {
        if (myDB == null || !myDB.isOpen() || contentValues == null) {
            getDB();
        }

        if (myDB == null) {
            return ResultConstant.FAILED;
        }

        int r = myDB.update(cur_tab_name, contentValues, SafeBox.Properties.KEY + "=?", new String[]{oldKey});
        if (r != -1) {
            return ResultConstant.SUCCESS;
        }
        return ResultConstant.FAILED;
    }

    public synchronized int deleteItem(String path) {

        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return -1;
        }

        return myDB.delete(cur_tab_name, SafeBox.Properties.DATA + "=?", new String[]{path});
    }

    public synchronized int delete(String selection, String[] selectionArgs) {

        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return -1;
        }

        return myDB.delete(cur_tab_name, selection, selectionArgs);
    }

    public void createTable() {
        if (myDB != null && myDB.isOpen()) {
            myDB.execSQL(CREATE_TABLE_FILE);
        }
    }

    public void UpgradeTable() {
        if (myDB != null && myDB.isOpen()) {
            myDB.execSQL(CREATE_TABLE_FILE);
        }
    }

    /**
     * check table is exist
     *
     * @return
     */
    public boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean result = false;
        if (db == null || !db.isOpen()) {
            return false;
        }

        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
                    + tableName + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public synchronized boolean setSafeValue(String value) {

        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return false;
        }

        DES des = new DES();
        value = des.encrypt(value);

        Cursor cursor = myDB.query(cur_tab_name, null, SafeBox.Properties.TITLE + "=?",
                new String[]{SAFE_KEY}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put(SafeBox.Properties.DATA, value);
                int r = myDB.update(cur_tab_name, values, SafeBox.Properties.TITLE + "=?",
                        new String[]{SAFE_KEY});
                if (r != -1) {
                    return true;
                }
            }

            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(SafeBox.Properties.TITLE, SAFE_KEY);
        values.put(SafeBox.Properties.DATA, value);
        myDB.insert(cur_tab_name, null, values);

        return true;
    }

    public String getSafeValue() {
        String safe = "";
        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return safe;
        }

        Cursor cursor = myDB.query(cur_tab_name, null, SafeBox.Properties.TITLE + "=?",
                new String[]{SAFE_KEY}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            safe = cursor.getString(cursor.getColumnIndex(SafeBox.Properties.DATA));
            cursor.close();

            DES des = new DES();
            safe = des.decrypt(safe);
            return safe;
        }

        return safe;
    }


    public boolean setSafeEmailValue(String value) {

        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return false;
        }

        DES des = new DES();
        value = des.encrypt(value);

        Cursor cursor = myDB.query(cur_tab_name, null, SafeBox.Properties.TITLE + "=?",
                new String[]{SAFE_EMAIL_KEY}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put(SafeBox.Properties.DATA, value);
                int r = myDB.update(cur_tab_name, values, SafeBox.Properties.TITLE + "=?",
                        new String[]{SAFE_EMAIL_KEY});

                if (r != -1) {
                    return true;
                }
            }

            cursor.close();
        }

        ContentValues values = new ContentValues();
        values.put(SafeBox.Properties.TITLE, SAFE_EMAIL_KEY);
        values.put(SafeBox.Properties.DATA, value);
        myDB.insert(cur_tab_name, null, values);

        return true;
    }

    public String getSafeEmailValue() {
        String email = "";
        if (myDB == null || !myDB.isOpen()) {
            getDB();
        }

        if (myDB == null) {
            return email;
        }

        Cursor cursor = myDB.query(cur_tab_name, null, SafeBox.Properties.TITLE + "=?",
                new String[]{SAFE_EMAIL_KEY}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            email = cursor.getString(cursor.getColumnIndex(SafeBox.Properties.DATA));
            cursor.close();

            DES des = new DES();
            email = des.decrypt(email);
            return email;
        }

        return email;
    }

    public static void closeDB() {
        if (helper != null) {
            helper = null;
        }

        if (myDB != null && myDB.isOpen()) {
            myDB.close();
        }
    }

    private ContentValues getContentValuesBySafeBox(SafeBox safeBox) {
        ContentValues values = new ContentValues();

        values.put(SafeBox.Properties.PARENT, safeBox.getParent());
        values.put(SafeBox.Properties.TITLE, safeBox.getTitle());
        values.put(SafeBox.Properties.SIZE, safeBox.getSize());
        values.put(SafeBox.Properties.DATE_MODIFIED, safeBox.getLastModified());
        values.put(SafeBox.Properties.MIME_TYPE, safeBox.getMimeType());
        values.put(SafeBox.Properties.DATA, safeBox.getData());
        values.put(SafeBox.Properties.REAL_PATH, safeBox.getRealPath());
        values.put(SafeBox.Properties.KEY, safeBox.getKey());
        values.put(SafeBox.Properties.IS_FILE, safeBox.getIsFolder());

        return values;
    }

    private SafeBox getSafeBoxByCursor(Cursor cursor) {
        SafeBox safeBox = new SafeBox();
        safeBox.setIsFolder(cursor.getInt(cursor.getColumnIndex(SafeBox.Properties.IS_FILE)));
        safeBox.setLastModified(cursor.getLong(cursor.getColumnIndex(SafeBox.Properties.DATE_MODIFIED)));
        safeBox.setMimeType(cursor.getString(cursor.getColumnIndex(SafeBox.Properties.MIME_TYPE)));
        safeBox.setTitle(cursor.getString(cursor.getColumnIndex(SafeBox.Properties.TITLE)));
        safeBox.setData(cursor.getString(cursor.getColumnIndex(SafeBox.Properties.DATA)));
        safeBox.setParent(cursor.getInt(cursor.getColumnIndex(SafeBox.Properties.PARENT)));
        safeBox.setRealPath(cursor.getString(cursor.getColumnIndex(SafeBox.Properties.REAL_PATH)));
        safeBox.setSize(cursor.getLong(cursor.getColumnIndex(SafeBox.Properties.SIZE)));
        safeBox.setKey(cursor.getString(cursor.getColumnIndex(SafeBox.Properties.KEY)));

        return safeBox;
    }

}

