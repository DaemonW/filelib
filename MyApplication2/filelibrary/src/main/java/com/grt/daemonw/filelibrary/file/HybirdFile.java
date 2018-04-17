package com.grt.daemonw.filelibrary.file;

import android.content.Context;

import com.grt.daemonw.filelibrary.reflect.Volume;
import com.grt.daemonw.filelibrary.utils.StorageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daemonw on 4/13/18.
 */

public class HybirdFile {
    public static final String TAG_EXTERNAL_STORAGE = "content://com.android.externalstorage.documents/tree/";
    public static final String TAG_TREE = "tree/";
    public static final String TAG_DOC = "document/";

    private AbstractFile mFile;

    public HybirdFile(Context context, String filePath, int type) {
        switch (type) {
            case AbstractFile.TYPE_INTERNAL:
                mFile = new LocalFile(context, filePath);
                break;
            case AbstractFile.TYPE_EXTERNAL:
                mFile = new DocFile(context, filePath, type);
                break;
            case AbstractFile.TYPE_USB:
                mFile = new DocFile(context, filePath, type);
                break;
        }
    }

    public HybirdFile(Context context, String filePath) {
        int type = AbstractFile.TYPE_INTERNAL;
        if (filePath.startsWith(TAG_EXTERNAL_STORAGE)) {
            int index = filePath.indexOf(TAG_DOC);
            String storageName;
            if(index==-1){
                storageName=filePath.substring(TAG_EXTERNAL_STORAGE.length());
            }else{
                storageName = filePath.substring(TAG_EXTERNAL_STORAGE.length(), index);
            }
            List<Volume> volumes = StorageUtil.getVolumes(context);
            for (Volume v : volumes) {
                if (v.mPath.contains(storageName)) {
                    type = v.mountType;
                    break;
                }
                String label = v.mDescription.toLowerCase();
                if (label.contains("usb") || label.contains("otg")) {
                    type = AbstractFile.TYPE_USB;
                    break;
                }

                if (label.contains("sd") || label.contains("ext")) {
                    type = AbstractFile.TYPE_EXTERNAL;
                    break;
                }
            }
        }
        switch (type) {
            case AbstractFile.TYPE_INTERNAL:
                mFile = new LocalFile(context, filePath);
                break;
            case AbstractFile.TYPE_EXTERNAL:
                mFile = new DocFile(context, filePath, type);
                break;
            case AbstractFile.TYPE_USB:
                mFile = new DocFile(context, filePath, type);
                break;
        }
    }

    public HybirdFile(AbstractFile abstractFile) {
        this.mFile = abstractFile;
    }


    public boolean delete() {
        return mFile.delete();
    }

    public AbstractFile createNewFile(String subFileName) throws IOException {
        return mFile.createNewFile(subFileName);
    }

    public HybirdFile mkDir(String subFolder) throws IOException {
        return new HybirdFile(mFile.mkDir(subFolder));
    }

    public String getName() {
        return mFile.getName();
    }

    public String getParent() {
        return mFile.getParent();
    }

    public AbstractFile getParentFile() {
        return mFile.getParentFile();
    }

    public String getPath() {
        return mFile.getPath();
    }

    public OutputStream getOutStream() throws IOException {
        return mFile.getOutStream();
    }

    public InputStream getInputStream() throws IOException {
        return mFile.getInputStream();
    }

    public int getFileType() {
        return mFile.getFileType();
    }

    public ArrayList<AbstractFile> listFiles() {
        return mFile.listFiles();
    }
}
