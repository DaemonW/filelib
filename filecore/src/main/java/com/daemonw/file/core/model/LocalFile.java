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
    private Filer delegate;
    private Context mContext;
    private String rootPath;
    private String rootUri;

    public LocalFile(Context context, String filePath, int type) {
        mContext = context;
        switch (type) {
            case Filer.TYPE_INTERNAL:
                delegate = new InternalFile(filePath);
                break;
            case Filer.TYPE_EXTERNAL:
                Volume v1 = StorageUtil.getMountVolume(context, type);
                if (v1 != null) {
                    rootPath = v1.mPath;
                    rootUri = StorageUtil.getMountUri(context, v1.mountType);
                    delegate = new SdFile(context, filePath, rootPath, rootUri);
                }
                break;
            case Filer.TYPE_USB:
                Volume v2 = StorageUtil.getMountVolume(context, type);
                if (v2 != null) {
                    rootPath = v2.mPath;
                    rootUri = StorageUtil.getMountUri(context, v2.mountType);
                    delegate = new UsbFile(context, filePath, rootPath, rootUri);
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
                    delegate = new InternalFile(filePath);
                    break;
                case Filer.TYPE_EXTERNAL:
                    rootPath = v.mPath;
                    rootUri = StorageUtil.getMountUri(context, v.mountType);
                    delegate = new SdFile(context, filePath, rootPath, rootUri);
                    break;
                case Filer.TYPE_USB:
                    rootPath = v.mPath;
                    rootUri = StorageUtil.getMountUri(context, v.mountType);
                    delegate = new UsbFile(context, filePath, rootPath, rootUri);
                    break;
            }
        }
    }

    @Override
    public boolean createNewFile() throws IOException {
        if (delegate == null) {
            return false;
        }
        return delegate.createNewFile();
    }

    @Override
    public boolean createChild(String name) throws IOException {
        return delegate.createChild(name);
    }

    @Override
    public boolean mkChild(String name) throws IOException {
        return delegate.mkChild(name);
    }

    @Override
    public boolean mkDir() throws IOException {
        if (delegate == null) {
            return false;
        }
        return delegate.mkDir();
    }

    @Override
    public String getPath() {
        if (delegate == null) {
            return null;
        }
        return delegate.getPath();
    }

    @Override
    public String getUri() {
        return delegate.getUri();
    }

    @Override
    public Filer getParentFile() {
        if (delegate == null) {
            return null;
        }
        return delegate.getParentFile();
    }

    @Override
    public String getParentPath() {
        if (delegate == null) {
            return null;
        }
        return delegate.getParentPath();
    }

    @Override
    public FileOutputStream getOutStream() throws IOException {
        if (delegate == null) {
            return null;
        }
        return delegate.getOutStream();
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        if (delegate == null) {
            return null;
        }
        return delegate.getInputStream();
    }

    @Override
    public boolean isDirectory() {
        if (delegate == null) {
            return false;
        }
        return delegate.isDirectory();
    }

    @Override
    public Filer[] listFiles() {
        if (delegate == null) {
            return null;
        }
        return delegate.listFiles();
    }

    @Override
    public long lastModified() {
        return delegate.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        if (delegate == null) {
            return false;
        }
        return delegate.hasChild(fileName);
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
        return delegate.equals(h.delegate);
    }

    @Override
    public boolean erase() throws IOException {
        if (delegate == null) {
            return false;
        }
        return delegate.erase();
    }

    @Override
    public String getName() {
        if (delegate == null) {
            return null;
        }
        return delegate.getName();
    }

    @Override
    public long length() {
        if (delegate == null) {
            return -1;
        }
        return delegate.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        if (delegate == null) {
            return false;
        }
        return delegate.renameTo(fileName);
    }

    @Override
    public boolean exists() {
        if (delegate == null) {
            return false;
        }
        return delegate.exists();
    }

    @Override
    public boolean delete() {
        if (delegate == null) {
            return false;
        }
        return delegate.delete();
    }

    @Override
    public int getType() {
        return delegate.getType();
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
