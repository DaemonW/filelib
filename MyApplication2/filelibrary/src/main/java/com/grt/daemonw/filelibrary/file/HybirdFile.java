package com.grt.daemonw.filelibrary.file;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;

import com.grt.daemonw.filelibrary.reflect.Volume;
import com.grt.daemonw.filelibrary.utils.MimeTypes;
import com.grt.daemonw.filelibrary.utils.StorageUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daemonw on 4/13/18.
 */

public class HybirdFile implements Filer {
    public static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";
    public static final String SEGMENT_TREE = "/tree/";
    public static final String SEGMENT_DOC = "/document/";

    private DocumentFile mFile;
    private Uri mUri;
    private String mPath;
    private Context mContext;
    private int mountType = Volume.MOUNT_UNKNOWN;


    public HybirdFile(Context context, String filePath) {
        mContext = context;
        if (isContentUri(filePath)) {
            mUri = Uri.parse(filePath);
            mPath = mUri.getPath();
            mFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
        } else {
            mUri = Uri.fromFile(new File(filePath));
            mPath = filePath;
            mFile = DocumentFile.fromFile(new File(filePath));
        }
        mountType = Volume.MOUNT_UNKNOWN;
    }

    public HybirdFile(Context context, DocumentFile file) {
        mContext = context;
        mFile = file;
        mUri = mFile.getUri();
        mPath = mUri.getPath();
        mountType = Volume.MOUNT_UNKNOWN;
    }


    @Override
    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public HybirdFile createNewFile(String fileName) throws IOException {
        if (!isDirectory()) {
            return null;
        }
        try {
            String name = new File(fileName).getName();
            DocumentFile newFile = mFile.createFile(MimeTypes.getMimeType(name), name);
            if (newFile != null) {
                return new HybirdFile(mContext, newFile);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public HybirdFile mkDir(String subFolder) throws IOException {
        try {
            File f = new File(subFolder);
            DocumentFile newDir = mFile.createDirectory(f.getName());
            if (newDir == null) {
                return null;
            }
            return new HybirdFile(mContext, newDir);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        String name = mFile.getName();
        if (name == null) {
            name = mUri.getLastPathSegment();
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
    public String getUri() {
        return mUri.toString();
    }

    @Override
    public HybirdFile getParentFile() {
        return new HybirdFile(mContext, mFile.getParentFile());
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public String getParentUri() {
        return mFile.getParentFile().getUri().toString();
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return mContext.getContentResolver().openOutputStream(mFile.getUri());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getContentResolver().openInputStream(mFile.getUri());
    }

    @Override
    public int getFileType() {
        return Filer.TYPE_LOCAL;
    }

    @Override
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> files = new ArrayList<>();
        DocumentFile[] subFiles = mFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (DocumentFile f : subFiles) {
            files.add(new HybirdFile(mContext, f));
        }
        return files;
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
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
}
