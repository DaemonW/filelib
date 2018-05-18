package com.daemonw.file.core.model;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.daemonw.file.core.utils.RawFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ExternalFile extends Filer {
    public static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";

    private File mRawFile;
    private DocFile mSafFile;
    private Context mContext;
    private String mRootPath;
    private String mRootUri;

    public ExternalFile(Context context, String filePath, String rootPath, String rootUri) {
        mContext = context;
        mPath = filePath;
        mRawFile = new File(filePath);
        mRootPath = rootPath;
        mRootUri = rootUri;
        mType = TYPE_EXTERNAL;
    }

    public ExternalFile(Context context, File file, String rootPath, String rootUri) {
        mContext = context;
        mPath = file.getAbsolutePath();
        mRawFile = file;
        mRootPath = rootPath;
        mRootUri = rootUri;
        mType = TYPE_EXTERNAL;
    }

    private ExternalFile(Context context, String filePath, String rootPath, String rootUri, DocFile file) {
        mContext = context;
        mRawFile = new File(filePath);
        mPath = mRawFile.getAbsolutePath();
        mRootPath = rootPath;
        mRootUri = rootUri;
        mSafFile = file;
        mType = TYPE_EXTERNAL;
    }

    @Override
    public boolean delete() {
        if (canRawWrite()) {
            return mRawFile.delete();
        }

        DocFile file = getDocumentFile();
        if (!file.exists()) {
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
            return new ExternalFile(mContext, newRawFile, mRootPath, mRootUri);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        DocFile newSafFile = file.createFile(name);
        if (newSafFile == null) {
            return null;
        }
        return new ExternalFile(mContext, mPath + "/" + name, mRootPath, mRootUri, newSafFile);
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
            return new ExternalFile(mContext, newFolder, mRootPath, mRootUri);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        DocFile newSafFolder = file.createDirectory(name);
        if (newSafFolder == null) {
            return null;
        }
        return new ExternalFile(mContext, mPath + "/" + name, mRootPath, mRootUri, newSafFolder);
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
    public String getUri() {
        if (canRawRead()) {
            return Uri.fromFile(mRawFile).toString();
        }
        DocFile f = getDocumentFile();
        if (!f.exists()) {
            return null;
        }
        return f.getUri().toString();
    }

    @Override
    public Filer getParentFile() {
        if (canRawRead()) {
            return new ExternalFile(mContext, mRawFile.getParentFile(), mRootPath, mRootUri);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        return new ExternalFile(mContext, file.getParent(), mRootPath, mRootUri, file.getParentFile());
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
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        return (FileOutputStream) mContext.getContentResolver().openOutputStream(file.getUri());
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        if (canRawRead()) {
            return new FileInputStream(mRawFile);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        return (FileInputStream) mContext.getContentResolver().openInputStream(file.getUri());
    }

    @Override
    public boolean isDirectory() {
        if (canRawRead()) {
            return mRawFile.isDirectory();
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }
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
                subFiles.add(new ExternalFile(mContext, f, mRootPath, mRootUri));
            }
        } else {
            DocFile file = getDocumentFile();
            if (!file.exists()) {
                return subFiles;
            }
            List<DocFile> subSaf = file.listFiles();
            if (subSaf == null || subSaf.size() <= 0) {
                return subFiles;
            }
            for (DocFile f : subSaf) {
                subFiles.add(new ExternalFile(mContext, mPath + "/" + f.getName(), mRootPath, mRootUri, f));
            }
        }
        return subFiles;
    }

    @Override
    public long lastModified() {
        if (canRawRead()) {
            return mRawFile.lastModified();
        }

        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return 0;
        }
        return file.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        String name = new File(fileName).getName();
        if (canRawRead()) {
            return new File(mRawFile, name).exists();
        }
        DocFile file = new DocFile(mContext, mPath + "/" + name, mRootPath, mRootUri);
        return file.exists();
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

        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        String name = new File(fileName).getName();
        if (canRawWrite()) {
            return mRawFile.renameTo(new File(mRawFile.getParent(), name));
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }

        boolean success = false;
        try {
            success = file.renameTo(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean exists() {
        if (canRawRead()) {
            return mRawFile.exists();
        }
        DocFile file = getDocumentFile();
        return file.exists();
    }

    @Override
    public boolean fillWithZero() throws IOException {
        if (canRawWrite()) {
            RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
            return RawFileUtil.fillWithZero(raf);
        } else {
            DocFile file = getDocumentFile();
            if (!file.exists()) {
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

    private DocFile getDocumentFile() {
        if (mSafFile != null) {
            return mSafFile;
        }
        DocFile file = new DocFile(mContext, mPath, mRootPath, mRootUri);
        mSafFile = file;
        return file;
    }
}
