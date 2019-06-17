package com.daemonw.file.core.model;

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

public class LocalFile extends Filer {
    private Filer fileDelegate;
    private Context mContext;
    private String rootPath;
    private String rootUri;

    public LocalFile(Context context, String filePath, int type) {
        mContext = context;
        switch (type) {
            case Filer.TYPE_INTERNAL:
                fileDelegate = new InternalFile(filePath);
                break;
            case Filer.TYPE_EXTERNAL:
                Volume v1 = StorageUtil.getMountVolume(context, type);
                if (v1 != null) {
                    rootPath = v1.mPath;
                    rootUri = StorageUtil.getMountUri(context, v1.mountType);
                    fileDelegate = new SdFile(context, filePath, rootPath, rootUri);
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

    public LocalFile(Context context, String filePath) {
        mContext = context;
        Volume v = getMountPoint(filePath);
        if (v != null) {
            switch (v.mountType) {
                case Filer.TYPE_INTERNAL:
                    fileDelegate = new InternalFile(filePath);
                    break;
                case Filer.TYPE_EXTERNAL:
                    rootPath = v.mPath;
                    rootUri = StorageUtil.getMountUri(context, v.mountType);
                    fileDelegate = new SdFile(context, filePath, rootPath, rootUri);
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
    public boolean createNewFile() throws IOException {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.createNewFile();
    }

    @Override
    public boolean createChild(String name) throws IOException {
        return fileDelegate.createChild(name);
    }

    @Override
    public boolean mkChild(String name) throws IOException {
        return fileDelegate.mkChild(name);
    }

    @Override
    public boolean mkDir() throws IOException {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.mkDir();
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
        if (!(o instanceof LocalFile)) {
            return false;
        }
        LocalFile h = (LocalFile) o;
        return fileDelegate.equals(h.fileDelegate);
    }

    @Override
    public boolean erase() throws IOException {
        if (fileDelegate == null) {
            return false;
        }
        return fileDelegate.erase();
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

    @Override
    public int getType() {
        return fileDelegate.getType();
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
