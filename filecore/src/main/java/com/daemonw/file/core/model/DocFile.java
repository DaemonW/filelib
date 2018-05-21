package com.daemonw.file.core.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import com.daemonw.file.core.utils.DocFileUtilApi19;
import com.daemonw.file.core.utils.DocFileUtilApi21;
import com.daemonw.file.core.utils.MimeTypes;
import com.daemonw.file.core.utils.StorageUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class DocFile {

    private final String[] DOCUMENT_PROJECTION = new String[]{
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_FLAGS
    };

    private Context mContext;
    private String mPath;
    private String mRootPath;
    private String mRootUri;
    private DocFile mParent;
    private String mDocumentId;
    private ContentResolver mCr;
    private String mName;
    private String mMimeType;
    private long mLength;
    private long mLastModified;
    private boolean mIsDirectory;
    private int mFlag;
    private Uri mUri;
    private boolean mExist;

    public DocFile(Context context, String filePath, String rootPath, String rootUri) {
        mContext = context;
        mCr = mContext.getContentResolver();
        mPath = filePath;
        mRootPath = rootPath;
        mRootUri = rootUri;
        if (mPath.equals(mRootPath)) {
            mParent = null;
        } else {
            String parentPath = new File(mPath).getParent();
            mParent = new DocFile(context, parentPath, rootPath, rootUri);
        }
        mDocumentId = StorageUtil.path2DocumentId(mPath, mRootPath, mRootUri);
        updateFileInfo();
    }


    private DocFile(Context context, String filePath, String rootPath, String rootUri, Cursor cursor) {
        mContext = context;
        mCr = mContext.getContentResolver();
        mPath = filePath;
        mRootPath = rootPath;
        mRootUri = rootUri;
        if (mPath.equals(mRootPath)) {
            mParent = null;
        } else {
            String parentPath = new File(mPath).getParent();
            mParent = new DocFile(context, parentPath, rootPath, rootUri);
        }
        mDocumentId = StorageUtil.path2DocumentId(mPath, mRootPath, mRootUri);
        mUri = DocumentsContract.buildDocumentUriUsingTree(Uri.parse(mRootUri), mDocumentId);
        mExist = true;
        try {
            mName = cursor.getString(0);
            mLength = cursor.getLong(1);
            mLastModified = cursor.getLong(2);
            mMimeType = cursor.getString(3);
            mIsDirectory = mMimeType.equals(DocumentsContract.Document.MIME_TYPE_DIR);
            mFlag = cursor.getInt(4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DocFile(Context context, String filePath, String rootPath, String rootUri, Uri uri) {
        mContext = context;
        mCr = mContext.getContentResolver();
        mPath = filePath;
        mRootPath = rootPath;
        mRootUri = rootUri;
        if (mPath.equals(mRootPath)) {
            mParent = null;
        } else {
            String parentPath = new File(mPath).getParent();
            mParent = new DocFile(context, parentPath, rootPath, rootUri);
        }
        mUri = uri;
        mDocumentId = DocumentsContract.getDocumentId(uri);
        updateFileInfo();
    }

    private void updateFileInfo() {
        if (mUri == null) {
            mUri = DocumentsContract.buildDocumentUriUsingTree(Uri.parse(mRootUri), mDocumentId);
        }
        Cursor cursor = mContext.getContentResolver().query(mUri, DOCUMENT_PROJECTION,
                null, null, null);
        if (cursor == null) {
            mExist = false;
            return;
        }
        try {
            if (cursor.moveToNext()) {
                mExist = true;
                mName = cursor.getString(0);
                mLength = cursor.getLong(1);
                mLastModified = cursor.getLong(2);
                mMimeType = cursor.getString(3);
                mIsDirectory = mMimeType.equals(DocumentsContract.Document.MIME_TYPE_DIR);
                mFlag = cursor.getInt(4);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(cursor);
        }
    }

    public String getPath() {
        return mPath;
    }

    public DocFile getParentFile() {
        return mParent;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public String getParent() {
        return mParent.getPath();
    }

    public String getDocumentId() {
        return mDocumentId;
    }

    public String getName() {
        return mName;
    }

    public long length() {
        return mLength;
    }

    public long lastModified() {
        return mLastModified;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public boolean isFile() {
        return !mIsDirectory;
    }

    public Uri getUri() {
        return mUri;
    }


    public boolean canRead() {
        // Ignore documents without MIME
        if (TextUtils.isEmpty(mMimeType)) {
            return false;
        }
        return true;
    }

    public boolean canWrite() {
        // Ignore documents without MIME
        if (TextUtils.isEmpty(mMimeType)) {
            return false;
        }
        // Deletable documents considered writable
        if ((mFlag & DocumentsContract.Document.FLAG_SUPPORTS_DELETE) != 0) {
            return true;
        }
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mMimeType)
                && (mFlag & DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE) != 0) {
            // Directories that allow create considered writable
            return true;
        } else if (!TextUtils.isEmpty(mMimeType)
                && (mFlag & DocumentsContract.Document.FLAG_SUPPORTS_WRITE) != 0) {
            // Writable normal files considered writable
            return true;
        }
        return false;
    }

    public DocFile createFile(String fileName) throws IOException {
        if (mUri == null) {
            return null;
        }
        String name = new File(fileName).getName();
        Uri childUri = DocumentsContract.createDocument(mCr, mUri, MimeTypes.getMimeType(name), name);
        if (childUri != null) {
            return new DocFile(mContext, mPath + "/" + name, mRootPath, mRootUri, childUri);
        }
        return null;
    }

    public DocFile createDirectory(String fileName) throws IOException {
        if (mUri == null) {
            return null;
        }
        String name = new File(fileName).getName();
        Uri childUri = DocumentsContract.createDocument(mCr, mUri, DocumentsContract.Document.MIME_TYPE_DIR, name);
        if (childUri != null) {
            return new DocFile(mContext, mPath + "/" + name, mRootPath, mRootUri, childUri);
        }
        return null;
    }

    public boolean delete() {
        return DocFileUtilApi19.delete(mContext, mUri);
    }

    public boolean exists() {
        return mExist;
    }

    public List<DocFile> listFiles() {
        Uri uri = DocumentsContract.buildChildDocumentsUriUsingTree(Uri.parse(mRootUri), mDocumentId);
        Cursor childCursor = mContext.getContentResolver().query(uri, DOCUMENT_PROJECTION,
                null, null, null);
        if (childCursor == null) {
            return null;
        }
        ArrayList<DocFile> subFiles = new ArrayList<>();
        try {
            while (childCursor.moveToNext()) {
                String name = childCursor.getString(0);
                subFiles.add(new DocFile(mContext, mPath + "/" + name, mRootPath, mRootUri, childCursor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(childCursor);
        }
        return subFiles;
    }

    public boolean isRoot() {
        return mParent == null;
    }

    public boolean renameTo(String fileName) throws IOException {
        String name = new File(fileName).getName();
        final Uri result = DocFileUtilApi21.renameTo(mContext, mUri, name);
        if (result != null) {
            mPath = getParent() + "./" + name;
            mName = name;
            mDocumentId = DocumentsContract.getDocumentId(mUri);
            mUri = result;
            return true;
        } else {
            return false;
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
