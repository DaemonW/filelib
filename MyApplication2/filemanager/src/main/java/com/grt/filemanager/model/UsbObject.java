package com.grt.filemanager.model;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.DocumentsContract;
import android.text.TextUtils;


import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.util.LogUtils;
import com.grt.filemanager.util.PreferenceUtils;
import com.grt.filemanager.util.TimeUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 2016/7/24 0024
 *          ${tags}
 */
public class UsbObject implements FileInfo {

    private String TAG = "UsbObject";
    private final static Context context = ApiPresenter.getContext();
    private final static ContentResolver resolver = context.getContentResolver();
    private static String baseUri = PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "");

    private UsbFileInfo mFile;

    public UsbObject(String path) {
        try {
//            LogToFile.e(TAG, "UsbObject Uri.parse(getDocumentFileUri(path) =" + getDocumentFileUri(path));
            Cursor cursor = resolver.query(Uri.parse(getDocumentFileUri(path)), null, null, null, null);
//            LogToFile.e(TAG, "UsbObject cursor =" + cursor);
            if (cursor != null) {
                cursor.moveToFirst();
                mFile = createUsbFileInfo(cursor, path);
                cursor.close();
            } else {
                initTmpFile(path);
            }
        } catch (Exception e) {
//            LogToFile.e(TAG, "UsbObject Exception ==" +e.getMessage());
            e.printStackTrace();
            initTmpFile(path);
        }
    }

    private void initTmpFile(String path) {
        mFile = new UsbFileInfo();
        mFile.setName(FileUtils.getFileName(path));
        mFile.setPath(path);
        mFile.setSize(0);
        mFile.setLastModified(TimeUtils.getCurrentTime());
        mFile.setIsFolder(false);
        mFile.setMimeType(FileUtils.getMiMeType(path));
    }

    public UsbObject(UsbFileInfo file) {
        this.mFile = file;
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
        return mFile.getLastModified();
    }

    @Override
    public long getSize() {
        return mFile.getSize();
    }

    @Override
    public String getMimeType() {
        return mFile.getMimeType();
    }

    @SuppressLint("NewApi")
    @Override
    public Bitmap getThumb(boolean isList) {
        return DocumentsContract.getDocumentThumbnail(resolver,
                Uri.parse(getDocumentFileUri(mFile.getPath())),
                new Point(isList ? 40 : 104, isList ? 40 : 72),
                new CancellationSignal());
    }

    @Override
    public Bundle getSpecialInfo(String type) {
        Bundle bundle = new Bundle();

        switch (type) {
            case DataConstant.PERMISSION:
                bundle.putString(type, mFile.isFolder() ? "drwxrwx--" : "-rw-rw----");
                break;

            case DataConstant.OPERATION_PERMISSION:
                bundle.putString(type, DataConstant.ALL_ALLOW_PERMISSION);
                break;

            case DataConstant.CAN_PREVIEW:
                bundle.putBoolean(type, !BuildUtils.thanMarshmallow());
                break;
        }

        return bundle;
    }

    @Override
    public boolean isFolder() {
        return mFile.isFolder();
    }

    @Override
    public List<FileInfo> getList(final boolean containHide) {
        List<FileInfo> children = getChildrenFromSAF(containHide);
        if (children == null) {

            try {
                UsbMassStorageDevice device = UsbMassStorageDevice.getMassStorageDevices(context)[0];
                if (StorageManagerUtil.getInstant().hasUsbPermission(device.getUsbDevice())) {
                    device.init();
                    device.close();

                    Thread.sleep(4000);

                    children = getChildrenFromSAF(containHide);
                } else {
                    children = new ArrayList<>();
                }
            } catch (Exception e) {
                e.printStackTrace();
                children = new ArrayList<>();
            }

        }

        return children;
    }

    @SuppressLint("NewApi")
    private List<FileInfo> getChildrenFromSAF(boolean containHide) {
        List<FileInfo> children = null;

        try {
//            LogToFile.e(TAG, TAG + " getDocumentFileUri(getPath()) = " + getDocumentFileUri(getPath()));
            Uri uri = Uri.parse(getDocumentFileUri(getPath()).concat("/children/"));
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//            LogToFile.e(TAG, "cursor ==" + (cursor == null));
            if (cursor != null) {
                children = new ArrayList<>();
                int count = cursor.getCount();
//                LogToFile.e(TAG, "count == " + count + "//path ==" + uri.getPath());
                if (count > 0) {
                    cursor.moveToFirst();
                    String name;
                    String path;
                    String parentPath = FileUtils.concatPath(mFile.getPath());
                    for (int index = 0; index < count; index++) {
                        name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                        if (!containHide && name.startsWith(".")) {
                            cursor.moveToNext();
                            continue;
                        }

                        path = parentPath.concat(name);
                        children.add(new UsbObject(createUsbFileInfo(cursor, path)));
                        cursor.moveToNext();
                    }
                }

                cursor.close();
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return children;
    }

    @SuppressLint("NewApi")
    public static UsbFileInfo createUsbFileInfo(Cursor cursor, String path) {
        UsbFileInfo file = new UsbFileInfo();

        file.setDocId(cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)));
        file.setLastModified(cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)));

        String name = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
        String mimeType = cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE));
        boolean isFolder = mimeType.equals(DocumentsContract.Document.MIME_TYPE_DIR);
        long size = cursor.getLong(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE));
        if (isFolder) {
            mimeType = DataConstant.MIME_TYPE_FOLDER;
            size = 0;
        }

        file.setName(name);
        file.setPath(path);
        file.setIsFolder(isFolder);
        file.setMimeType(mimeType);
        file.setSize(size);

        return file;
    }

    @Override
    public InputStream getInputStream() {
        Uri uri = Uri.parse(getDocumentFileUri(mFile.getPath()));
        try {
            return resolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        Uri uri = Uri.parse(getDocumentFileUri(mFile.getPath()));
        try {
            return resolver.openOutputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("NewApi")
    @Override
    public int create(boolean isFolder) {
        String name = mFile.getName();
        String mimeType = isFolder ? DocumentsContract.Document.MIME_TYPE_DIR : FileUtils.getMiMeType(name);
        Uri uri = Uri.parse(getDocumentFileUri(FileUtils.getParentPath(mFile.getPath())));
        Uri u = null;
        u = DocumentsContract.createDocument(resolver, uri, mimeType, name);

        boolean result = u != null;
        if (result) {
            mFile.setIsFolder(isFolder);
            if (isFolder) {
                mFile.setMimeType(DataConstant.MIME_TYPE_FOLDER);
            }
        }

        return u != null ? ResultConstant.SUCCESS : ResultConstant.FAILED;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean rename(String newPath) {
        Uri uri = Uri.parse(getDocumentFileUri(mFile.getPath()));
        String newName = FileUtils.getFileName(newPath);
        Uri u = null;
        u = DocumentsContract.renameDocument(resolver, uri, newName);
        boolean result = u != null;
        if (result) {
            mFile.setPath(newPath);
            mFile.setName(newName);
        }

        return result;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean delete() {
        return DocumentsContract.deleteDocument(resolver, Uri.parse(getDocumentFileUri(mFile.getPath())));
    }

    @Override
    public boolean exists() {
        boolean result;
        try {
            Cursor cursor = resolver.query(Uri.parse(getDocumentFileUri(mFile.getPath())), null, null, null, null);
            result = cursor != null && cursor.getCount() > 0;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public static String getDocumentFileUri(String path) {
        String usbPath = StorageManagerUtil.getInstant().getUsbPath();
        if (TextUtils.isEmpty(baseUri)) {
            baseUri = PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "");
        }

        if (path.equals(usbPath)) {
            return baseUri;
        } else {
            usbPath = FileUtils.concatPath(usbPath);
            path = path.substring(usbPath.length());
            return baseUri + Uri.encode(path);
        }
    }

    private static class UsbFileInfo {

        private String mDocId;
        private String mName;
        private String mPath;
        private long mSize;
        private boolean mIsFolder;
        private String mMimeType;
        private long mLastModified;

        public UsbFileInfo() {
        }

        public String getDocId() {
            return mDocId;
        }

        public void setDocId(String docId) {
            this.mDocId = docId;
        }

        public String getPath() {
            return mPath;
        }

        public void setPath(String filePath) {
            this.mPath = filePath;
        }

        public String getName() {
            return mName;
        }

        public long getSize() {
            return mSize;
        }

        public boolean isFolder() {
            return mIsFolder;
        }

        public String getMimeType() {
            return mMimeType;
        }

        public long getLastModified() {
            return mLastModified;
        }

        public void setName(String fileName) {
            this.mName = fileName;
        }

        public void setSize(long size) {
            this.mSize = size;
        }

        public void setIsFolder(boolean isFolder) {
            this.mIsFolder = isFolder;
        }

        public void setMimeType(String mimeType) {
            this.mMimeType = mimeType;
        }

        public void setLastModified(long lastModified) {
            this.mLastModified = lastModified;
        }

    }

}
