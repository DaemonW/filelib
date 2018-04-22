package com.daemonw.filelib.model;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.daemonw.filelib.utils.MimeTypes;
import com.daemonw.filelib.utils.RawFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by daemonw on 4/13/18.
 */

public class LocalFile implements Filer {
    public static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";

    private DocumentFile mSafFile;
    private File mRawFile;
    private String mPath;
    private Context mContext;
    private int mType = Filer.TYPE_RAW;
    private boolean isChecked;


    public LocalFile(Context context, String filePath) {
        mContext = context;
        if (isContentUri(filePath)) {
            mSafFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
            mPath = mSafFile.getUri().toString();
            mType = Filer.TYPE_SAF;
        } else {
            mRawFile = new File(filePath);
            mPath = mRawFile.getAbsolutePath();
            mType = Filer.TYPE_RAW;
        }
    }

    public LocalFile(Context context, DocumentFile file) {
        mContext = context;
        mSafFile = file;
        mPath = file.getUri().toString();
        mType = Filer.TYPE_SAF;
    }

    public LocalFile(Context context, File file) {
        mContext = context;
        mRawFile = file;
        mPath = file.getAbsolutePath();
        mType = Filer.TYPE_RAW;
    }


    @Override
    public boolean delete() {
        return isRaw() ? RawFileUtil.delete(mPath) : mSafFile.delete();
    }

    @Override
    public LocalFile createNewFile(String fileName) throws IOException {
        if (!isDirectory()) {
            return null;
        }
        try {
            String name = new File(fileName).getName();
            boolean success = false;
            if (isRaw()) {
                File newRawFile = new File(mRawFile, name);
                success = newRawFile.createNewFile();
                if (success) {
                    return new LocalFile(mContext, newRawFile);
                }
            } else {
                DocumentFile newSafFile = mSafFile.createFile(MimeTypes.getMimeType(name), name);
                if (newSafFile != null) {
                    return new LocalFile(mContext, newSafFile);
                }
            }

        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public LocalFile mkDir(String folderName) throws IOException {
        if (!isDirectory()) {
            return null;
        }
        try {
            String name = new File(folderName).getName();
            if (isRaw()) {
                File newRawFolder = new File(mRawFile, name);
                boolean success = newRawFolder.mkdir();
                if (success) {
                    return new LocalFile(mContext, newRawFolder);
                }
            } else {
                DocumentFile newSafFolder = mSafFile.createDirectory(name);
                if (newSafFolder != null) {
                    return new LocalFile(mContext, newSafFolder);
                }
            }

        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public String getName() {
        if (isRaw()) {
            return mRawFile.getName();
        }
        String name = mSafFile.getName();
        if (name == null) {
            name = mSafFile.getUri().getLastPathSegment();
            int index = name.lastIndexOf(":");
            if (index != -1) {
                name = name.substring(index + 1);
            }
        }
        return name;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public LocalFile getParentFile() {
        if (isRaw()) {
            return new LocalFile(mContext, mRawFile.getParentFile());
        }
        return new LocalFile(mContext, mSafFile.getParentFile());
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        if (isRaw()) {
            return new FileOutputStream(mRawFile);
        }
        return mContext.getContentResolver().openOutputStream(mSafFile.getUri());
    }


    public RandomAccessFile getRandomAccessFile() throws IOException {
        if (mType != TYPE_RAW) {
            return null;
        }
        return new RandomAccessFile(mPath, "rw");
    }

    public ParcelFileDescriptor getParcelFileDescriptor() throws IOException {
        if (mType != TYPE_SAF) {
            return null;
        }
        return mContext.getContentResolver().openFileDescriptor(mSafFile.getUri(), "rw");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (isRaw()) {
            return new FileInputStream(mRawFile);
        }
        return mContext.getContentResolver().openInputStream(mSafFile.getUri());
    }

    @Override
    public int getFileType() {
        return mType;
    }

    @Override
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> files = new ArrayList<>();
        if (isRaw()) {
            File[] subRaw = mRawFile.listFiles();
            if (subRaw != null && subRaw.length > 0) {
                for (File f : subRaw) {
                    files.add(new LocalFile(mContext, f));
                }
            }
        } else {
            DocumentFile[] subSaf = mSafFile.listFiles();
            if (subSaf != null && subSaf.length > 0) {
                for (DocumentFile f : subSaf) {
                    files.add(new LocalFile(mContext, f));
                }
            }
        }
        return files;
    }

    @Override
    public boolean isDirectory() {
        return isRaw() ? mRawFile.isDirectory() : mSafFile.isDirectory();
    }

    @Override
    public long lastModified() {
        return isRaw() ? mRawFile.lastModified() : mSafFile.lastModified();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isRaw() {
        return mType == Filer.TYPE_RAW;
    }

    @Override
    public boolean hasChild(String name) {
        if (mType == TYPE_RAW) {
            return new File(mPath, name).exists();
        }
        DocumentFile child = mSafFile.findFile(name);
        return child != null;
    }

    @Override
    public long length() {
        if (mType == TYPE_RAW) {
            return mRawFile.length();
        }
        return mSafFile.length();
    }

    private boolean isContentUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith("content://");
    }

    private boolean isFileUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith("file://");
    }


    private boolean isExternalStorageUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith(EXTERNAL_STORAGE_URI);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        LocalFile f = (LocalFile) obj;
        return f.mPath.equals(this.mPath);
    }
}
