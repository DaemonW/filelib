package com.grt.filemanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SecurityUtils {

    private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String VALID_KEY = "Si8eY091SIUk80d2";

    public static String getMD5(InputStream inputStream, AtomicBoolean cancel) {
        try {
            byte[] buffer = new byte[8192]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                if (cancel != null && cancel.get()) {
                    return "";
                }
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    public static String toHexString(byte[] b, AtomicBoolean cancel) {
        if (b == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            if (cancel != null && cancel.get()) {
                return "";
            }
            sb.append(hexChar[(aB & 0xf0) >>> 4]);
            sb.append(hexChar[aB & 0x0f]);
        }
        return sb.toString();
    }

    private static String toHexString(byte[] b) {
        if (b == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte aB : b) {
            sb.append(hexChar[(aB & 0xf0) >>> 4]);
            sb.append(hexChar[aB & 0x0f]);
        }

        return sb.toString();
    }

    public static String computeMd5(String s) {
        if (s == null) {
            return "";
        }

        return computeMd5(s.getBytes());
    }

    public static String computeMd5(byte[] b) {
        if (b == null) {
            return "";
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return "";
        }

        md.update(b);

        return toHexString(md.digest());
    }

    public static String md5String(String string) {

        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
            return toHexString(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String md5StringForPhoneValid(String string) {
        string += VALID_KEY;
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
            return toHexString(hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFileChecksum(String filePath, String algorithms) {
        try {

            File file = new File(filePath);
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());

            MessageDigest messageDigest = MessageDigest.getInstance(algorithms);
            messageDigest.update(byteBuffer);

            return toHexString(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getSHA1(InputStream inputStream, AtomicBoolean cancel) {
        try {
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                if (cancel != null && cancel.get()) {
                    return "";
                }
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] sha1Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    private static String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (byte hashByte : hashBytes) {
            returnVal += Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }

    public static byte[] sha1(String val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] data = val.getBytes("utf-8");
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    public static String hexdigest(byte[] paramArrayOfByte) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            int i = 0;
            int j = 0;
            while (true) {
                if (i >= 16)
                    return new String(arrayOfChar);
                int k = arrayOfByte[i];
                int m = j + 1;
                arrayOfChar[j] = hexChar[(0xF & k >>> 4)];
                j = m + 1;
                arrayOfChar[m] = hexChar[(k & 0xF)];
                i++;
            }
        } catch (Exception localException) {
        }
        return null;
    }

}
