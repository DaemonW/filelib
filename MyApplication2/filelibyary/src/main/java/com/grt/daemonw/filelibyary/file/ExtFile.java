package com.grt.daemonw.filelibyary.file;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;

import com.grt.daemonw.filelibyary.MimeTypes;
import com.grt.daemonw.filelibyary.exception.PermException;
import com.grt.daemonw.filelibyary.utils.ExtFileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExtFile extends HybirdFile {
    public static final String PREF_DEFAULT_EXT_URI = "EXT_STORAGE_URI";

    private File mFile;
    private DocumentFile mDocFile;
    private Uri mRootUri;
    private Context mContext;

    private ExtFile(String filePath, Context context) {
        super(filePath);
        mFile = new File(filePath);
        mType=HybirdFile.TYPE_FILE;
        mContext = context;
        String strUri = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PREF_DEFAULT_EXT_URI, null);
        if (strUri == null) {
            throw new PermException(PermException.PERM_EXT);
        }
        mRootUri = Uri.parse(strUri);
        mDocFile = ExtFileUtil.getDocumentFile(mContext, mPath, mRootUri);
    }

    private ExtFile(DocumentFile file, Context context) {
        super(file.getUri().getPath());
        mFile = new File(mPath);
        mType=HybirdFile.TYPE_FILE;
        mContext = context;
        String strUri = PreferenceManager.getDefaultSharedPreferences(mContext).getString(PREF_DEFAULT_EXT_URI, null);
        if (strUri == null) {
            throw new PermException(PermException.PERM_EXT);
        }
        mRootUri = Uri.parse(strUri);
        mDocFile = ExtFileUtil.getDocumentFile(mContext, mPath, mRootUri);
    }

    @Override
    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public boolean createNewFile() throws IOException {
        try {
            return mDocFile.createFile(MimeTypes.getMimeType(mFile), mFile.getName()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean mkDir() {
        try {
            File f = new File(mPath);
            return mDocFile.createDirectory(f.getName()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return mDocFile.getName();
    }

    @Override
    public String getParent() {
        return mDocFile.getParentFile().getUri().getPath();
    }

    @Override
    public HybirdFile getParentFile() {
        return new ExtFile(mDocFile.getParentFile(), mContext);
    }

    @Override
    public String getPath() {
        return mFile.getPath();
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return mContext.getContentResolver().openOutputStream(mDocFile.getUri());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mContext.getContentResolver().openInputStream(mDocFile.getUri());
    }

    @Override
    public int getFileType() {
        return HybirdFile.TYPE_EXT;
    }

    @Override
    public ArrayList<? extends HybirdFile> listFiles() {
        ArrayList<ExtFile> files = new ArrayList<>();
        DocumentFile[] subFiles = mDocFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (DocumentFile f : subFiles) {
            files.add(new ExtFile(f, mContext));
        }
        return files;
    }
}
