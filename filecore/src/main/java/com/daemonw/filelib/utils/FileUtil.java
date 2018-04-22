package com.daemonw.filelib.utils;

import com.daemonw.filelib.model.Filer;
import com.daemonw.filelib.model.LocalFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileUtil {

    public static void save(byte[] data, Filer file) {
        OutputStream out = null;
        try {
            out = file.getOutStream();
            IOUtil.save(data, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(out);
        }
    }


    public static boolean copyFile(Filer srcFile, Filer dstFolder) {
        if (srcFile == null) {
            return false;
        }
        OutputStream out = null;
        InputStream in = null;
        try {
            Filer dstFile = null;
            String name = srcFile.getName();
            if (!dstFolder.hasChild(name)) {
                dstFile = dstFolder.createNewFile(name);
            }
            if (dstFile == null) {
                return false;
            }
            in = srcFile.getInputStream();
            out = dstFile.getOutStream();
            IOUtil.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(in);
            IOUtil.closeStream(out);
        }
        return true;
    }

    public static boolean delete(Filer file, int eraseCount) {
        if (!file.isDirectory()) {
            return deleteFile(file, eraseCount);
        } else {
            ArrayList<Filer> sub = file.listFiles();
            if (sub == null || sub.size() == 0) {
                return file.delete();
            } else {
                for (Filer f : sub) {
                    delete(f, eraseCount);
                }
            }
        }
        return delete(file, eraseCount);
    }

    public static boolean deleteFile(Filer file, int eraseCount) {
        if (eraseCount <= 0) {
            return file.delete();
        }
        boolean success = true;
        for (int i = 0; i < eraseCount; i++) {
            success = success && erase(file);
        }
        success = success && file.delete();
        return success;
    }

    public static boolean erase(Filer file) {
        if (!(file instanceof LocalFile)) {
            return false;
        }
        boolean success = true;
        LocalFile f = (LocalFile) file;
        try {
            if (f.getFileType() == Filer.TYPE_RAW) {
                success = RawFileUtil.erase(f.getRandomAccessFile());
            } else {
                success = RawFileUtil.erase(f.getParcelFileDescriptor(), f.length());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

}
