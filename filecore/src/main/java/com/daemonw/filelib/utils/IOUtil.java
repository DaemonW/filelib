package com.daemonw.filelib.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/****************************
 *  提供输入/输出流的IO接口
 */

public class IOUtil {
    public static final int KB = 1024;

    public static byte[] readAll(InputStream in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int nRead = 0;
        byte[] buffer = new byte[4096];
        try {
            while ((nRead = in.read(buffer)) != -1) {
                bos.write(buffer, 0, nRead);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
        }
        return bos.toByteArray();
    }

    public static void save(byte[] data, OutputStream out) {
        int size = data.length;
        try {
            if (size <= 4 * KB) {
                out.write(data);
                out.flush();
            } else {
                ByteArrayInputStream bin = new ByteArrayInputStream(data);
                byte[] buff = new byte[4096];
                int nRead = 0;
                while ((nRead = bin.read(buff)) != -1) {
                    out.write(buff, 0, nRead);
                }
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(out);
        }
    }


    public static void copy(InputStream in, OutputStream out) {
        try {
            byte[] buff = new byte[4096];
            int nRead = 0;
            while ((nRead = in.read(buff)) != -1) {
                out.write(buff, 0, nRead);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }


    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
