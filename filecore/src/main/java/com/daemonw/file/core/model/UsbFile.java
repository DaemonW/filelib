package com.daemonw.file.core.model;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class UsbFile extends ExternalFile {

    public UsbFile(Context context, String filePath, String rootPath, String rootUri) {
        super(context, filePath, rootPath, rootUri);
    }

    public UsbFile(Context context, File file, String rootPath, String rootUri) {
        super(context, file, rootPath, rootUri);
    }

    public UsbFile(Context context, String filePath, String rootPath, String rootUri, DocFile file) {
        super(context, filePath, rootPath, rootUri, file);
    }

    @Override
    public Filer getParentFile() {
        if (canRawRead()) {
            return new UsbFile(mContext, mRawFile.getParentFile(), mRootPath, mRootUri);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return null;
        }
        return new UsbFile(mContext, file.getParent(), mRootPath, mRootUri, file.getParentFile());
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
                subFiles.add(new UsbFile(mContext, f, mRootPath, mRootUri));
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
                subFiles.add(new UsbFile(mContext, mPath + "/" + f.getName(), mRootPath, mRootUri, f));
            }
        }
        return subFiles;
    }

    @Override
    public int getType() {
        return TYPE_USB;
    }
}