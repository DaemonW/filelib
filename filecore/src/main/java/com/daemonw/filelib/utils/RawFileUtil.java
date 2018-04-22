package com.daemonw.filelib.utils;

import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RawFileUtil {

    public static boolean mkFile(String filePath) {
        boolean success = false;
        try {
            success = new File(filePath).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean mkDir(String folderName) {
        boolean success = false;
        try {
            success = new File(folderName).mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            return rm(file);
        } else {
            File[] sub = file.listFiles();
            if (sub == null || sub.length == 0) {
                return rm(file);
            } else {
                for (File f : sub) {
                    delete(f.getAbsolutePath());
                }
            }
        }
        return rm(file);
    }

    private static boolean rm(File file) {
        return file.delete();
    }

    public static boolean erase(RandomAccessFile file) {
        boolean success = true;
        try {
            byte[] buff = new byte[4096];
            long left = file.length();
            while (left >= buff.length) {
                file.write(buff);
                left -= buff.length;
            }
            file.write(buff, 0, (int) left);
            file.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtil.closeStream(file);
        }
        return success;
    }

    public static boolean erase(ParcelFileDescriptor file, long length) {
        boolean success = true;
        ParcelFileDescriptor.AutoCloseOutputStream out = new ParcelFileDescriptor.AutoCloseOutputStream(file);
        try {
            byte[] buff = new byte[4096];
            long left = length;
            while (left >= buff.length) {
                out.write(buff);
                left -= buff.length;
            }
            out.write(buff, 0, (int) left);
            out.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtil.closeStream(out);
        }
        return success;
    }
}
