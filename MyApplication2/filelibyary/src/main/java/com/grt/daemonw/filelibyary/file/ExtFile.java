package com.grt.daemonw.filelibyary.file;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import com.grt.daemonw.filelibyary.MimeTypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExtFile extends Filer {

    private DocumentFile mDocFile;
    private Context mContext;

    public ExtFile(Context context, String filePath) {
        super(filePath);
        mType = Filer.TYPE_EXT;
        mContext = context;
        mDocFile = DocumentFile.fromTreeUri(context, Uri.parse(filePath));
    }

    private ExtFile(Context context, DocumentFile file) {
        super(file.getUri().toString());
        mType = Filer.TYPE_EXT;
        mContext = context;
        mDocFile = file;
    }

    @Override
    public boolean delete() {
        return mDocFile.delete();
    }

    @Override
    public ExtFile createNewFile(String fileName) throws IOException {
        if (!isDirectory()) {
            return null;
        }
        try {
            String name = new File(fileName).getName();
            DocumentFile newFile = mDocFile.createFile(MimeTypes.getMimeType(name), name);
            if (newFile != null) {
                return new ExtFile(mContext, newFile);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return null;
    }

    @Override
    public ExtFile mkDir(String folderName) throws IOException {
        try {
            File f = new File(folderName);
            DocumentFile dir = mDocFile.createDirectory(f.getName());
            if (dir == null) {
                return null;
            }
            return new ExtFile(mContext, dir);
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
    public Filer getParentFile() {
        return new ExtFile(mContext, mDocFile.getParentFile());
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
        return Filer.TYPE_EXT;
    }

    @Override
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> files = new ArrayList<>();
        DocumentFile[] subFiles = mDocFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (DocumentFile f : subFiles) {
            files.add(new ExtFile(mContext, f));
        }
        return files;
    }

    @Override
    public boolean isDirectory() {
        return mDocFile.isDirectory();
    }
}
