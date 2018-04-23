package com.daemonw.filelib.model;

import android.app.Activity;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.daemonw.filelib.utils.MimeTypes;
import com.daemonw.filelib.utils.RawFileUtil;
import com.daemonw.filelib.utils.StorageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class UsbFile extends Filer {

    public static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";

    private File mRawFile;
    private DocumentFile mSafFile;
    private boolean isRawPath;
    private Activity mContext;

    public UsbFile(Activity context, String filePath) {
        mContext = context;
        mPath = filePath;
        mType = TYPE_USB;
        if (isContentUri(filePath)) {
            Uri uri = Uri.parse(filePath);
            mSafFile = DocumentFile.fromTreeUri(context, uri);
            isRawPath = false;
        } else {
            mRawFile = new File(filePath);
            isRawPath = true;
        }
    }

    private UsbFile(Activity context, File file) {
        mContext = context;
        mPath = file.getAbsolutePath();
        mType = TYPE_USB;
        mRawFile = file;
        isRawPath = true;
    }

    private UsbFile(Activity context, DocumentFile file) {
        mContext = context;
        mPath = file.getUri().toString();
        mType = TYPE_USB;
        mSafFile = file;
        isRawPath = false;
    }

    @Override
    public boolean delete() {
        if (canRawWrite()) {
            return mRawFile.delete();
        }
        DocumentFile file = getDocumentFile();
        return file.delete();
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        String name = new File(fileName).getName();
        if (canRawWrite()) {
            File newRawFile = new File(mRawFile, name);
            newRawFile.createNewFile();
            return new UsbFile(mContext, newRawFile);
        }
        DocumentFile file = getDocumentFile();
        DocumentFile newSafFile = file.createFile(MimeTypes.getMimeType(name), name);
        if (newSafFile == null) {
            return null;
        }
        return new UsbFile(mContext, newSafFile);
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
            return new UsbFile(mContext, newFolder);
        }
        DocumentFile file = getDocumentFile();
        DocumentFile newSafFolder = file.createDirectory(name);
        if (newSafFolder == null) {
            return null;
        }
        return new UsbFile(mContext, newSafFolder);
    }

    @Override
    public String getName() {
        if (isRawPath) {
            return mRawFile.getName();
        }
        return mSafFile.getName();
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public Filer getParentFile() {
        if (canRawRead()) {
            return new UsbFile(mContext, mRawFile.getParentFile());
        }
        DocumentFile file = getDocumentFile();
        return new UsbFile(mContext, file.getParentFile());
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        if (canRawWrite()) {
            return new FileOutputStream(mRawFile);
        }
        DocumentFile file = getDocumentFile();
        return mContext.getContentResolver().openOutputStream(file.getUri());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (canRawRead()) {
            return new FileInputStream(mRawFile);
        }
        DocumentFile file = getDocumentFile();
        return mContext.getContentResolver().openInputStream(file.getUri());
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
                subFiles.add(new UsbFile(mContext, f));
            }
        } else {
            DocumentFile file = getDocumentFile();
            DocumentFile[] subSaf = file.listFiles();
            if (subSaf == null || subSaf.length <= 0) {
                return subFiles;
            }
            for (DocumentFile f : subSaf) {
                subFiles.add(new UsbFile(mContext, f));
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

        if (!(o instanceof UsbFile)) {
            return false;
        }
        UsbFile f = (UsbFile) o;
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
    public void fillWithZero() throws IOException {
        if (canRawWrite()) {
            RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
            RawFileUtil.fillWithZero(raf);
        } else {
            DocumentFile file = getDocumentFile();
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(mSafFile.getUri(), "rw");
            RawFileUtil.fillWithZero(pfd, length());
        }
    }


    public boolean canRawWrite() {
        return isRawPath && mRawFile.canWrite();
    }

    public boolean canRawRead() {
        return isRawPath && mRawFile.canRead();
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
        try {
            String rootPath = StorageUtil.getMountPath(mContext, mType);
            String rootUri = StorageUtil.getMountUri(mContext, mType);
            file = StorageUtil.findDocumentFile(mContext, mPath, rootPath, rootUri);
            mSafFile = file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
