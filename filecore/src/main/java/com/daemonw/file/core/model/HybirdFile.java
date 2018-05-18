package com.daemonw.file.core.model;

import android.app.Activity;
import android.content.Context;

import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.StorageUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daemonw on 4/13/18.
 */

public class HybirdFile extends Filer {
    private Filer fileDelegate;
    private Context mContext;
    private String rootPath;
    private String rootUri;

    public HybirdFile(Activity context, String filePath, int type) {
        mContext = context;
        switch (type) {
            case Filer.TYPE_INTERNAL:
                fileDelegate = new RawFile(filePath);
                break;
            case Filer.TYPE_EXTERNAL:
                Volume v1 = StorageUtil.getMountVolume(context, type);
                if (v1 != null) {
                    rootPath = v1.mPath;
                    rootUri = StorageUtil.getMountUri(context, v1.mountType);
                    fileDelegate = new ExternalFile(context, filePath, rootPath, rootUri);
                }
                break;
            case Filer.TYPE_USB:
                Volume v2 = StorageUtil.getMountVolume(context, type);
                if (v2 != null) {
                    rootPath = v2.mPath;
                    rootUri = StorageUtil.getMountUri(context, v2.mountType);
                    fileDelegate = new UsbFile(context, filePath, rootPath, rootUri);
                }
                break;
        }
    }

    public HybirdFile(Context context, String filePath) {
        mContext = context;
        Volume v = getMountPoint(filePath);
        if (v != null) {
            switch (v.mountType) {
                case Filer.TYPE_INTERNAL:
                    fileDelegate = new RawFile(filePath);
                    break;
                case Filer.TYPE_EXTERNAL:
                    rootPath = v.mPath;
                    rootUri = StorageUtil.getMountUri(context, v.mountType);
                    fileDelegate = new ExternalFile(context, filePath, rootPath, rootUri);
                    break;
                case Filer.TYPE_USB:
                    rootPath = v.mPath;
                    rootUri = StorageUtil.getMountUri(context, v.mountType);
                    fileDelegate = new UsbFile(context, filePath, rootPath, rootUri);
                    break;
            }
        }
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.createNewFile(fileName);
    }

    @Override
    public Filer mkDir(String folderName) throws IOException {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.mkDir(folderName);
    }

    @Override
    public String getPath() {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getPath();
    }

    @Override
    public String getUri() {
        return fileDelegate.getUri();
    }

    @Override
    public Filer getParentFile() {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getParentFile();
    }

    @Override
    public String getParentPath() {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getParentPath();
    }

    @Override
    public FileOutputStream getOutStream() throws IOException {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getOutStream();
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getInputStream();
    }

    @Override
    public boolean isDirectory() {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.isDirectory();
    }

    @Override
    public ArrayList<Filer> listFiles() {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.listFiles();
    }

    @Override
    public long lastModified() {
        return fileDelegate.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.hasChild(fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HybirdFile)) {
            return false;
        }
        HybirdFile h = (HybirdFile) o;
        return fileDelegate.equals(h.fileDelegate);
    }

    @Override
    public boolean fillWithZero() throws IOException {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.fillWithZero();
    }

    @Override
    public String getName() {
        if (fileDelegate == null) {
            return null;
        }
        return fileDelegate.getName();
    }

    @Override
    public long length() {
        if (fileDelegate == null) {
            return -1;
        }
        return fileDelegate.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.renameTo(fileName);
    }

    @Override
    public boolean exists() {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.exists();
    }

    @Override
    public boolean delete() {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.delete();
    }

    private Volume getMountPoint(String filePath) {
        List<Volume> volumes = StorageUtil.getVolumes(mContext);
        for (Volume v : volumes) {
            if (filePath.startsWith(v.mPath)) {
                return v;
            }
        }
        return null;
    }
}
