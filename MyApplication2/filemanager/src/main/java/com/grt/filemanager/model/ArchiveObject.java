package com.grt.filemanager.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.imp.local.archive.EntryNode;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.util.LocalFileHelper;
import com.grt.filemanager.util.ShellUtils;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class ArchiveObject implements ArchiveFileInfo {

    protected static Context context = ApiPresenter.getContext();
    protected static final int BUFFER_LEN = 1024 * 80;

    protected abstract String getArchiveFilePath();

    @Override
    public InputStream getInputStream() {
        String path = getArchiveFilePath();
        if (path != null) {
            return LocalFileHelper.getInputStream(path);
        }

        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        String path = getArchiveFilePath();
        if (path != null) {
            return LocalFileHelper.getOutputStream(path);
        }

        return null;
    }

    @Override
    public int create(boolean isFolder) {
        String path = getArchiveFilePath();
        if (path != null) {
            return LocalFileHelper.createFile(path, false);
        }

        return ResultConstant.FAILED;
    }

    @Override
    public boolean rename(String newPath) {
        boolean result = false;
        String path = getArchiveFilePath();

        if (path != null) {
            result = LocalFileHelper.renameCompressObject(path, newPath);
        }

        return result;
    }

    @Override
    public Bundle getSpecialInfo(String type) {
        Bundle bundle = new Bundle();

        switch (type) {
            case DataConstant.IS_ENCRYPTED:
                bundle.putBoolean(type, isEncrypted());
                break;

            case DataConstant.PERMISSION:
                String path = getArchiveFilePath();
                if (!TextUtils.isEmpty(path)) {
                    if (StorageManagerUtil.getInstant().isMemoryPath(path)) {
                        bundle.putString(type, ShellUtils.getPermission(path));
                    } else {
                        bundle.putString(type, "-rw-rw----");
                    }
                }
                break;

            default:
                break;
        }

        return bundle;
    }

    protected static String getRealDesFolderPath(Bundle args, String desDirPath) {
        String realDesFolder;
        if (args != null) {
            realDesFolder = args.getString(ArchiveConstant.REAL_PATH, desDirPath);
        } else {
            realDesFolder = desDirPath;
        }

        return realDesFolder;
    }

    protected static void addNode(ArrayMap<String, EntryNode> entryArray, String archiveName,
                                  Object header, String archivePath, EntryNode parentNode) {
        boolean isFolder = archiveName.endsWith("/");
        EntryNode child;
        String path;

        if (archiveName.contains("/")) {

            String[] names = archiveName.split("/");
            String tmpName = "";
            int count = names.length;
            EntryNode tmpParent = parentNode;
            int lastIndex = count - 1;

            for (int index = 0; index < count; index++) {
                tmpName += "/" + names[index];
                path = archivePath.concat(tmpName);

                child = entryArray.get(path);
                if (child == null) {
                    if (index == lastIndex) {
                        isFolder = count == 1 && isFolder;
                        child = new EntryNode(path, isFolder, header);
                    } else {
                        child = new EntryNode(path, true, null);
                    }
                }

                tmpParent.addChild(path, child);
                tmpParent = child;
                entryArray.put(path, tmpParent);
            }

        } else {
            path = archivePath.concat("/").concat(archiveName);
            child = new EntryNode(getRightPath(path), isFolder, header);
            parentNode.addChild(path, child);
        }

    }

    protected static String getRightPath(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        }

        return path;
    }

}
