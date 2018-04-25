package com.daemonw.file.core.utils;

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

    public static boolean delete(File file, int eraseCount) {
        if (file.isDirectory()) {
            File[] subFile = file.listFiles();
            if (subFile.length > 0) {
                for (File f : subFile) {
                    delete(f, eraseCount);
                }
            }
        }
        return deleteFile(file, eraseCount);
    }

    private static boolean deleteFile(File file, int eraseCount) {
        if (eraseCount <= 0 || file.isDirectory()) {
            return file.delete();
        }
        boolean success = true;
        for (int i = 0; i < eraseCount; i++) {
            try {
                success = success && fillWithZero(new RandomAccessFile(file, "rw"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        success = success && file.delete();
        return success;
    }

    public static boolean fillWithZero(RandomAccessFile file) {
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

    public static boolean fillWithZero(ParcelFileDescriptor file, long length) {
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
