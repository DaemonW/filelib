package com.daemonw.file.core.model;

import android.content.Context;
import android.net.Uri;

import com.daemonw.file.core.utils.DocFileUtilApi19;
import com.daemonw.file.core.utils.DocFileUtilApi21;

import java.io.IOException;

class DocFile {
    private Context mContext;
    private Uri mUri;
    private DocFile mParent;

    DocFile(Context context, Uri uri) {
        mContext = context;
        mUri = uri;
    }

    public DocFile createFile(String mimeType, String displayName) throws IOException {
        final Uri result = DocFileUtilApi21.createFile(mContext, mUri, mimeType, displayName);
        return (result != null) ? new DocFile(mContext, result) : null;
    }

    public DocFile createDirectory(String displayName) throws IOException {
        final Uri result = DocFileUtilApi21.createDirectory(mContext, mUri, displayName);
        return (result != null) ? new DocFile(mContext, result) : null;
    }

    public Uri getUri() {
        return mUri;
    }

    public String getName() {
        return DocFileUtilApi19.getName(mContext, mUri);
    }

    public String getType() {
        return DocFileUtilApi19.getType(mContext, mUri);
    }

    public boolean isDirectory() {
        return DocFileUtilApi19.isDirectory(mContext, mUri);
    }

    public boolean isFile() {
        return DocFileUtilApi19.isFile(mContext, mUri);
    }

    public boolean isVirtual() {
        return DocFileUtilApi19.isVirtual(mContext, mUri);
    }

    public long lastModified() {
        return DocFileUtilApi19.lastModified(mContext, mUri);
    }

    public long length() {
        return DocFileUtilApi19.length(mContext, mUri);
    }

    public boolean canRead() {
        return DocFileUtilApi19.canRead(mContext, mUri);
    }

    public boolean canWrite() {
        return DocFileUtilApi19.canWrite(mContext, mUri);
    }

    public boolean delete() {
        return DocFileUtilApi19.delete(mContext, mUri);
    }

    public boolean exists() {
        return DocFileUtilApi19.exists(mContext, mUri);
    }

    public DocFile[] listFiles() {
        final Uri[] result = DocFileUtilApi21.listFiles(mContext, mUri);
        final DocFile[] resultFiles = new DocFile[result.length];
        for (int i = 0; i < result.length; i++) {
            resultFiles[i] = new DocFile(mContext, result[i]);
        }
        return resultFiles;
    }

    public boolean renameTo(String displayName) throws IOException {
        final Uri result = DocFileUtilApi21.renameTo(mContext, mUri, displayName);
        if (result != null) {
            mUri = result;
            return true;
        } else {
            return false;
        }
    }

    public DocFile findFile(String displayName) {
        for (DocFile doc : listFiles()) {
            if (displayName.equals(doc.getName())) {
                return doc;
            }
        }
        return null;
    }

    public DocFile getParentFile() {
        return mParent;
    }
}
