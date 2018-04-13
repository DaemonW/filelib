package com.grt.filemanager.util;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by LDC on 2018/3/7.
 */

public class LocalFileHelper {
    private static ContentResolver cr = ApiPresenter.getContext().getContentResolver();
    private static String TAG = "LocalFileHelper";

    public static boolean deleteCompressObject(String path) {
        if (useASF(path)) {
            return openUriLollipop() != null && delete(path);
        } else {
            if (StorageManagerUtil.getInstant().isMemoryPath(path)) {
                return LocalFileHelper.deleteMemoryFile(path);
            } else {
                return new File(path).delete();
            }
        }
    }

    public static boolean deleteMemoryFile(String path) {
        boolean result = ShellUtils.deleteFile(path);
        if (result) {
            result = !ShellUtils.exists(path);
        }
        return result;
    }

//    public static boolean isExists(String path) {
//        if (useASF(path)) {
//            return openUriLollipop() != null && exists(path);
//        } else {
//            return new File(path).exists();
//        }
//
//    }
//
//    private static boolean useASF(String path) {
//        return BuildUtils.thanLollipop() && inSecondStorage(path) && permissionDenied();
//    }
//
//    public static boolean inSecondStorage(String filePath) {
//        String path = StorageManager.getStorageManager().getSecondaryStorage();
//
//        return !TextUtils.isEmpty(path) && filePath.startsWith(path);
//    }
//
//    private static boolean permissionDenied() {
//        return PreferenceUtils.getPrefBoolean(DataConstant.STORAGE_PERMISSION_DENIED, true);
//    }

    public static boolean inUsbStorage(String filePath) {
        String path = StorageManagerUtil.getInstant().getUsbPath();

//        LogToFile.e(TAG, "inUsbStorage path =" +path);
//        LogToFile.e(TAG, "inUsbStorage filepath =" +filePath);
        return !TextUtils.isEmpty(path) && filePath.startsWith(path);
    }

    public static InputStream getInputStream(String path) {
        if (useASF(path)) {
            if (useDocumentFile()) {
                return getInputStreamForDocumentFile(path);
            }
        }

        try {
            return new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static boolean useASF(String path) {
        return BuildUtils.thanLollipop() && inSecondStorage(path) && permissionDenied();
    }

    public static boolean inSecondStorage(String filePath) {
        String path = StorageManagerUtil.getInstant().getSecondaryStorage();

        return !TextUtils.isEmpty(path) && filePath.startsWith(path);
    }

    private static boolean permissionDenied() {
        return PreferenceUtils.getPrefBoolean(DataConstant.STORAGE_PERMISSION_DENIED, true);
    }

    public static boolean useDocumentFile() {
        return BuildUtils.thanLollipop() && openUriLollipop() != null;
    }

    public static String openUriLollipop() {
        String externalSdcardLollipop = PreferenceUtils.getPrefString(SettingConstant.EXTARNAL_SDCARD_LOLLIPOP, null);
        if (externalSdcardLollipop != null) {
            return externalSdcardLollipop;
        }
        return null;
    }

    public static InputStream getInputStreamForDocumentFile(String path) {
        Uri uri = getDocumentFileUri(path);
        try {
            return cr.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Uri getDocumentFileUri(String path) {
        String exSdcard = StorageManagerUtil.getInstant().getSecondaryStorage();
        String realPath = openUriLollipop();

        if (path.equals(exSdcard)) {
            return Uri.parse(realPath);
        } else {
            if (!exSdcard.endsWith(File.separator)) {
                exSdcard += File.separator;
            }
            path = path.substring(exSdcard.length());
            path = Uri.encode(path);
            realPath += path;
            return Uri.parse(realPath);
        }
    }

    public static OutputStream getOutputStream(String path) {
        if (useASF(path)) {
            if (useDocumentFile()) {
                File file = new File(path);
                if (!file.exists() && !create(file)) {
                    return null;
                }

                return getOutputStreamForDocumentFile(path);
            } else {
                return null;
            }
        }

        try {
            return new BufferedOutputStream(new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("NewApi")
    public static boolean create(File file) {
        String fileType = getFileType(file.getName());
        Uri uri = getDocumentFileUri(file.getParent());
        Uri u = null;
        u = DocumentsContract.createDocument(cr, uri, fileType, file.getName());
        return u != null;
    }

    private static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                return fileName.substring(typeIndex + 1).toLowerCase();
            }
        }
        return "";
    }

    public static OutputStream getOutputStreamForDocumentFile(String path) {
        Uri uri = getDocumentFileUri(path);
        try {
            return cr.openOutputStream(uri);
        } catch (Exception e) {
            LogUtils.e(TAG + "--getOutputStreamForDocumentFile", e.getMessage());
        }
        return null;
    }

    public static boolean rename(String path, String newPath) {
        File file = new File(path);
        boolean result;

        if (BuildUtils.thanLollipop() && LocalFileHelper.inSecondStorage(path)) {
            result = openUriLollipop() != null && rename(file, new File(newPath));
        } else {
            result = file.renameTo(new File(newPath));
        }

        return result;
    }

    @SuppressLint("NewApi")
    public static boolean rename(File oldFile, File newFile) {
        Uri uri = getDocumentFileUri(oldFile.getPath());
        Uri u = null;
        u = DocumentsContract.renameDocument(cr, uri, newFile.getName());
        return u != null;
    }

    /**
     * file.exists()在BuildUtils.thanLollipop()判断有延迟不准确，所以只在
     *
     * @param path     文件路径
     * @param isFolder 是否为文件夹
     * @return {@link ResultConstant}操作结果
     */
    public static int createFile(String path, boolean isFolder) {
        boolean result;
        File file = new File(path);

        if (isFolder) {
            if (useASF(path)) {
                if (exists(path)) {
                    return ResultConstant.EXIST;
                } else {
                    result = useDocumentFile() && mkDir(file);
                }
            } else {
                if (file.exists()) {
                    return ResultConstant.EXIST;
                } else {
                    result = file.mkdirs();
                }
            }
        } else {
            if (useASF(path)) {
                if (exists(path)) {
                    return ResultConstant.EXIST;
                } else {
                    result = useDocumentFile() && create(file);
                }
            } else {
                try {
                    if (file.exists()) {
                        return ResultConstant.EXIST;
                    } else {
                        result = file.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResultConstant.FAILED;
                }
            }
        }

        return result ? ResultConstant.SUCCESS : ResultConstant.FAILED;
    }

    @SuppressLint("NewApi")
    public static boolean exists(String path) {
        boolean exist = false;
        Cursor cursor = null;
        try {
            cursor = cr.query(getDocumentFileUri(path), null, null, null, null);
            if (cursor != null) {
                exist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exist;
    }

    @SuppressLint("NewApi")
    public static boolean mkDir(File file) {
        Uri uri = getDocumentFileUri(file.getParent());
        Uri u = null;
        u = DocumentsContract.createDocument(cr, uri,
                DocumentsContract.Document.MIME_TYPE_DIR, file.getName());
        return u != null;
    }

    public static boolean deleteFile(String path) {
        if (useASF(path)) {
            return openUriLollipop() != null && delete(path);
        } else {
            return new File(path).delete();
        }
    }

    @SuppressLint("NewApi")
    public static boolean delete(String path) {
        Uri uri = getDocumentFileUri(path);
        return DocumentsContract.deleteDocument(cr, uri);
    }

    public static boolean isExists(String path) {
        if (useASF(path)) {
            return openUriLollipop() != null && exists(path);
        } else {
            return new File(path).exists();
        }

    }

    public static boolean renameCompressObject(String path, String newPath) {
        File file = new File(path);
        boolean result;

        if (BuildUtils.thanLollipop() && LocalFileHelper.inSecondStorage(path)) {
            result = openUriLollipop() != null && rename(file, new File(newPath));
        } else {
            if (StorageManagerUtil.getInstant().isMemoryPath(path)) {
                return LocalFileHelper.renameMemoryFile(path, newPath);
            } else {
                result = file.renameTo(new File(newPath));
            }
        }

        return result;
    }

    public static boolean renameMemoryFile(String path, String newPath) {
        boolean result = false;
        try {
            result = ShellUtils.move(path, newPath);
            if (result) {
                result = ShellUtils.exists(newPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
