package com.grt.daemonw.filelibyary.file;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by daemonw on 4/13/18.
 */

public class HybirdFile {
    private Filer mFile;

    public HybirdFile(Context context, String filePath, int type) {
        switch (type) {
            case Filer.TYPE_FILE:
                mFile = new LocalFile(context, filePath);
                break;
            case Filer.TYPE_EXT:
                mFile = new ExtFile(context, filePath);
                break;
            case Filer.TYPE_USB:
                break;
        }
    }

    public HybirdFile(Filer filer){
        this.mFile=filer;
    }


    public boolean delete() {
        return mFile.delete();
    }

    public Filer createNewFile(String subFileName) throws IOException {
        return mFile.createNewFile(subFileName);
    }

    public HybirdFile mkDir(String subFolder) throws IOException{
        return new HybirdFile(mFile.mkDir(subFolder));
    }

    public String getName() {
        return mFile.getName();
    }

    public String getParent() {
        return mFile.getParent();
    }

    public Filer getParentFile() {
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

    public ArrayList<? extends Filer> listFiles() {
        return mFile.listFiles();
    }
}
