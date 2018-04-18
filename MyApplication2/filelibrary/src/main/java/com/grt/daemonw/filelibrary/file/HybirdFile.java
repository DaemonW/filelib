package com.grt.daemonw.filelibrary.file;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.grt.daemonw.filelibrary.MimeTypes;
import com.grt.daemonw.filelibrary.reflect.Volume;
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

public class HybirdFile extends Filer {
    public static final String TAG_EXTERNAL_STORAGE = "content://com.android.externalstorage.documents/tree/";
    public static final String TAG_TREE = "/tree/";
    public static final String TAG_DOC = "/document/";

    private DocumentFile mFile;
    private Context mContext;
    private int mountType = Volume.MOUNT_UNKNOWN;


    public HybirdFile(Context context, String filePath) {
        super(filePath);
        mContext = context;
        if (filePath.startsWith(TAG_EXTERNAL_STORAGE)) {
            mFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
        } else {
            mFile = DocumentFile.fromFile(new File(filePath));
        }
        mountType = Volume.MOUNT_UNKNOWN;
    }

    public HybirdFile(Context context, DocumentFile file) {
        super(file.getUri().toString());
        mContext = context;
        this.mFile = file;
        mountType = Volume.MOUNT_UNKNOWN;
    }


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

    public String getName() {
        return mFile.getName();
    }

    public String getParent() {
        return mFile.getParentFile().getUri().getPath();
    }

    public HybirdFile getParentFile() {
        return new HybirdFile(mContext, mFile.getParentFile());
    }

    public String getPath() {
        return mPath;
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return mContext.getContentResolver().openOutputStream(mFile.getUri());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getContentResolver().openInputStream(mFile.getUri());
    }

    public int getFileType() {
        if (mountType == Volume.MOUNT_UNKNOWN) {
            mountType = getMountVolume();
        }
        return mountType;
    }

    public int getMountVolume() {
        String volumeName = null;
        if (mPath.startsWith(TAG_EXTERNAL_STORAGE)) {
            int index = mPath.indexOf(TAG_DOC);
            if (index == -1) {
                volumeName = mPath.substring(TAG_EXTERNAL_STORAGE.length() + 1);
            } else {
                volumeName = mPath.substring(TAG_EXTERNAL_STORAGE.length(), index);
            }
            volumeName = new File(URLDecoder.decode(volumeName)).getName();
        }

        List<Volume> volumes = StorageUtil.getVolumes(mContext);
        for (Volume v : volumes) {
            if (v.mPath.contains(volumeName)) {
                return v.mountType;
            }
        }

        return Volume.MOUNT_INTERNAL;
    }

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
}
