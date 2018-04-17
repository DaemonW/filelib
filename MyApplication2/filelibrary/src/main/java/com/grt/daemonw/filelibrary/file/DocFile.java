package com.grt.daemonw.filelibrary.file;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.grt.daemonw.filelibrary.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DocFile extends AbstractFile {

    private DocumentFile mDocFile;
    private Context mContext;

    public DocFile(Context context, String filePath) {
        super(filePath);
        mType = AbstractFile.TYPE_EXTERNAL;
        mContext = context;
        mDocFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
    }

    public DocFile(Context context, String filePath, int type) {
        super(filePath);
        mType = type;
        mContext = context;
        mDocFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
    }

    private DocFile(Context context, DocumentFile file) {
        super(file.getUri().toString());
        mType = AbstractFile.TYPE_EXTERNAL;
        mContext = context;
        mDocFile = file;
    }

    @Override
    public boolean delete() {
        return mDocFile.delete();
    }

    @Override
    public DocFile createNewFile(String fileName) throws IOException {
        if (!isDirectory()) {
            return null;
        }
        try {
            String name = new File(fileName).getName();
            DocumentFile newFile = mDocFile.createFile(MimeTypes.getMimeType(name), name);
            if (newFile != null) {
                return new DocFile(mContext, newFile);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public DocFile mkDir(String folderName) throws IOException {
        try {
            File f = new File(folderName);
            DocumentFile dir = mDocFile.createDirectory(f.getName());
            if (dir == null) {
                return null;
            }
            return new DocFile(mContext, dir);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
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
    public AbstractFile getParentFile() {
        return new DocFile(mContext, mDocFile.getParentFile());
    }

    @Override
    public String getPath() {
        return mPath;
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
        return mType;
    }

    @Override
    public ArrayList<AbstractFile> listFiles() {
        ArrayList<AbstractFile> files = new ArrayList<>();
        DocumentFile[] subFiles = mDocFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (DocumentFile f : subFiles) {
            files.add(new DocFile(mContext, f));
        }
        return files;
    }

    @Override
    public boolean isDirectory() {
        return mDocFile.isDirectory();
    }
}
