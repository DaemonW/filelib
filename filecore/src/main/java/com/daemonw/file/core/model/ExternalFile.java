package com.daemonw.file.core.model;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.MimeTypes;
import com.daemonw.file.core.utils.RawFileUtil;
import com.daemonw.file.core.utils.StorageUtil;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ExternalFile extends Filer {
    public static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";

    private File mRawFile;
    private DocumentFile mSafFile;
    private Context mContext;

    public ExternalFile(Context context, String filePath) {
        mContext = context;
        mPath = filePath;
        mRawFile = new File(filePath);
        mType = TYPE_EXTERNAL;
    }

    public ExternalFile(Context context, File file) {
        mContext = context;
        mPath = file.getAbsolutePath();
        mRawFile = file;
        mType = TYPE_EXTERNAL;
    }

    private ExternalFile(Context context, String filePath, DocumentFile file) {
        mContext = context;
        mType = TYPE_EXTERNAL;
        mRawFile = new File(filePath);
        mPath = mRawFile.getAbsolutePath();
        mSafFile = file;
        mType = TYPE_EXTERNAL;
    }

    @Override
    public boolean delete() {
        if (canRawWrite()) {
            return mRawFile.delete();
        }

        DocumentFile file = getDocumentFile();
        if (file == null) {
            return false;
        }
        return file.delete();
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        String name = new File(fileName).getName();
        if (canRawWrite()) {
            File newRawFile = new File(mRawFile, name);
            newRawFile.createNewFile();
            return new ExternalFile(mContext, newRawFile);
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return null;
        }
        DocumentFile newSafFile = file.createFile(MimeTypes.getMimeType(name), name);
        if (newSafFile == null) {
            return null;
        }
        return new ExternalFile(mContext, mPath + "/" + name, newSafFile);
    }

    @Override
    public Filer mkDir(String folderName) throws IOException {
        String name = new File(folderName).getName();
        if (canRawWrite()) {
            File newFolder = new File(mRawFile, name);
            boolean success = newFolder.mkdir();
            if (!success) {
                throw new IOException("create directory failed");
            }
            return new ExternalFile(mContext, newFolder);
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return null;
        }
        DocumentFile newSafFolder = file.createDirectory(name);
        if (newSafFolder == null) {
            return null;
        }
        return new ExternalFile(mContext, mPath + "/" + name, newSafFolder);
    }

    @Override
    public String getName() {
        return mRawFile.getName();
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public Filer getParentFile() {
        if (canRawRead()) {
            return new ExternalFile(mContext, mRawFile.getParentFile());
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return null;
        }
        DocumentFile parent = file.getParentFile();
        return new ExternalFile(mContext, mRawFile.getParent(), parent);
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public FileOutputStream getOutStream() throws IOException {
        if (canRawWrite()) {
            return new FileOutputStream(mRawFile);
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return null;
        }
        return (FileOutputStream) mContext.getContentResolver().openOutputStream(file.getUri());
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        if (canRawRead()) {
            return new FileInputStream(mRawFile);
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return null;
        }
        return (FileInputStream) mContext.getContentResolver().openInputStream(file.getUri());
    }

    @Override
    public boolean isDirectory() {
        if (canRawRead()) {
            return mRawFile.isDirectory();
        }
        DocumentFile file = getDocumentFile();
        return file.isDirectory();
    }

    @Override
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> subFiles = new ArrayList<>();
        if (canRawRead()) {
            File[] subRaw = mRawFile.listFiles();
            if (subRaw == null || subRaw.length <= 0) {
                return subFiles;
            }
            for (File f : subRaw) {
                subFiles.add(new ExternalFile(mContext, f));
            }
        } else {
            DocumentFile file = getDocumentFile();
            if (file == null) {
                return subFiles;
            }
            DocumentFile[] subSaf = file.listFiles();
            if (subSaf == null || subSaf.length <= 0) {
                return subFiles;
            }
            for (DocumentFile f : subSaf) {
                subFiles.add(new ExternalFile(mContext, mPath + "/" + f.getName(), f));
            }
        }
        return subFiles;
    }

    @Override
    public long lastModified() {
        if (canRawRead()) {
            return mRawFile.lastModified();
        }

        DocumentFile file = getDocumentFile();
        return file.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        String name = new File(fileName).getName();
        if (canRawRead()) {
            return new File(mRawFile, name).exists();
        }
        DocumentFile file = getDocumentFile();
        return file.findFile(name) != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof ExternalFile)) {
            return false;
        }
        ExternalFile f = (ExternalFile) o;
        return f.mPath.equals(mPath);
    }

    @Override
    public long length() {
        if (canRawRead()) {
            return mRawFile.length();
        }

        DocumentFile file = getDocumentFile();
        return file.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        String name = new File(fileName).getName();
        if (canRawWrite()) {
            return mRawFile.renameTo(new File(mRawFile.getParent(), name));
        }
        DocumentFile file = getDocumentFile();
        return file.renameTo(name);
    }

    @Override
    public boolean exists() {
        if (canRawRead()) {
            return mRawFile.exists();
        }
        DocumentFile file = getDocumentFile();
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    @Override
    public boolean fillWithZero() throws IOException {
        if (canRawWrite()) {
            RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
            return RawFileUtil.fillWithZero(raf);
        } else {
            DocumentFile file = getDocumentFile();
            if (file == null) {
                return false;
            }
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(file.getUri(), "rw");
            return RawFileUtil.fillWithZero(pfd, length());
        }
    }

    public boolean canRawWrite() {
        return mRawFile.canWrite();
    }

    public boolean canRawRead() {
        boolean canRead = mRawFile.canRead();
        if (canRead) {
            return true;
        }

        //特殊文件,不可读写
        if (getName().equals(".android_secure")) {
            return true;
        }
        return false;
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

    private DocumentFile getDocumentFile() {
        if (mSafFile != null) {
            return mSafFile;
        }
        DocumentFile file = null;
        Volume volume = null;
        List<Volume> volumes = StorageUtil.getVolumes(mContext);
        for (Volume v : volumes) {
            if (mPath.startsWith(v.mPath)) {
                volume = v;
                break;
            }
        }
        if (volume == null) {
            Logger.e("can't found document file from path " + mPath);
            return null;
        }
        String rootUri = StorageUtil.getMountUri(mContext, volume.mountType);
        file = StorageUtil.findDocumentFile(mContext, mPath, volume.mPath, rootUri);
        mSafFile = file;
        return file;
    }
}
