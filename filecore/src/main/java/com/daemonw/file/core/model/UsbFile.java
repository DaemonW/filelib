package com.daemonw.file.core.model;

import android.content.Context;

import java.io.File;
import java.util.List;

class UsbFile extends ExternalFile {

    UsbFile(Context context, String filePath, String rootPath, String rootUri) {
        super(context, filePath, rootPath, rootUri);
    }

    private UsbFile(Context context, File file, String rootPath, String rootUri) {
        super(context, file, rootPath, rootUri);
    }

    private UsbFile(Context context, String filePath, String rootPath, String rootUri, DocFile file) {
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
    public Filer[] listFiles() {
        Filer[] subFiles = null;
        if (canRawRead()) {
            File[] subRaw = mRawFile.listFiles();
            if (subRaw == null || subRaw.length <= 0) {
                return null;
            }
            subFiles = new Filer[subRaw.length];
            for (int i = 0; i < subRaw.length; i++) {
                subFiles[i] = new UsbFile(mContext, subRaw[i], mRootPath, mRootUri);
            }
        } else {
            DocFile file = getDocumentFile();
            if (!file.exists()) {
                return null;
            }
            List<DocFile> subSaf = file.listFiles();
            if (subSaf == null || subSaf.size() <= 0) {
                return null;
            }
            subFiles = new Filer[subSaf.size()];
            for (int i = 0; i < subSaf.size(); i++) {
                DocFile f = subSaf.get(i);
                subFiles[i] = new UsbFile(mContext, mPath + "/" + f.getName(), mRootPath, mRootUri, f);
            }
        }
        return subFiles;
    }

    @Override
    public int getType() {
        return TYPE_USB;
    }
}