package com.daemonw.file.core.utils;

import com.daemonw.file.core.model.Filer;

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
                boolean success = dstFolder.createChild(name);
                if (!success) {
                    return false;
                }
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
        if (file.isDirectory()) {
            ArrayList<Filer> subFile = file.listFiles();
            if (subFile.size() > 0) {
                for (Filer f : subFile) {
                    delete(f, eraseCount);
                }
            }
        }
        return deleteFile(file, eraseCount);
    }

    public static boolean deleteFile(Filer file, int eraseCount) {
        if (eraseCount <= 0 || file.isDirectory()) {
            return file.delete();
        }
        boolean success = true;
        for (int i = 0; i < eraseCount; i++) {
            success = success && erase(file);
        }
        success = success && file.delete();
        return success;
    }

    private static boolean erase(Filer file) {
        try {
            return file.erase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
