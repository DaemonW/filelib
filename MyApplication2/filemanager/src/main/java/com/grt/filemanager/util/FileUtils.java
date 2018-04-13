package com.grt.filemanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Display;
import android.webkit.MimeTypeMap;

import com.grt.filemanager.model.FileInfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liucheng on 2015/10/30.
 */
public class FileUtils {

    private FileUtils() {
    }

    private static List<String> illegalCharList;

    static {
        String[] illegalChars = new String[]{"\\", "/", ":", "*",
                "?", "\"", "<", ">", "|"};

        illegalCharList = Arrays.asList(illegalChars);
    }

    public static boolean hasIllegalChar(String name) {
        for (int index = 0; index < name.length(); index++) {
            if (illegalCharList.contains(String.valueOf(name.charAt(index)))) {
                return true;
            }
        }

        return false;
    }

    public static boolean fileNameOnlyOne(String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }

        if (name.contains(".")) {
            String tmpString = name.trim();
            if (tmpString.length() == 1) {
                return true;
            }
        }

        return false;
    }

    public static boolean getThumbFromDb(String mimeType) {
        return mimeType != null && ((mimeType.startsWith("audio") ||
                mimeType.equals("application/ogg") ||
                mimeType.startsWith("video") ||
                mimeType.startsWith("image")));
    }

    public static boolean isApk(String mimeType) {
        return mimeType != null && mimeType.equals("application/vnd.android.package-archive");
    }

    public static boolean isAudio(String mimeType) {
        return mimeType != null && (mimeType.startsWith("audio")
                || mimeType.equals("application/ogg"));
    }

    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isHide(String name) {
        return !TextUtils.isEmpty(name) && name.startsWith(".");
    }

    public static boolean isSupportVideo(String videoPath) {
        if (null == videoPath || videoPath.equals("")) {
            return false;
        }
        String suffix = videoPath.substring(videoPath.lastIndexOf("."));

        return (suffix.equals(".3gp") || suffix.equals(".3GP")
                || suffix.equals(".mp4") || suffix.equals(".MP4"));
    }

    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isJPG(String path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length()).equals("jpg");
    }

    /**
     * 获取文件MIME
     *
     * @param name 文件名称
     * @return 文件MIME
     */
    public static String getMiMeType(String name) {
        String type;
        String extension = name.substring(name.lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        type = mime.getMimeTypeFromExtension(extension.toLowerCase());

        if (type == null) {
            switch (extension) {
                case "mkv":
                    type = "video/x-matroska";
                    break;

                case "xps":
                    type = "application/xps";
                    break;

                case "umd":
                    type = "application/umd";
                    break;

                case "chm":
                    type = "application/chm";
                    break;

                case "help":
                    type = "application/help";
                    break;

                case "epub":
                    type = "application/epub";
                    break;

                case "7z":
                    type = "application/x-7z-compressed";
                    break;

                case "jar":
                    type = "application/java-archive";
                    break;

                case "log":
                case "conf":
                case "config":
                case "ini":
                case "inf":
                case "sh":
                    type = "text/plain";
                    break;

                case "mp3":
                    type = "audio/mpeg";
                    break;

                case "wav":
                    type = "audio/x-wav";
                    break;

                case "au":
                case "snd":
                    type = "audio/basic";
                    break;
                case "mid":
                case "rmi":
                    type = "audio/mid";
                    break;

                case "aif":
                case "aifc":
                case "aiff":
                    type = "audio/x-aiff";
                    break;

                case "m3u":
                    type = "audio/x-mpegurl";
                    break;

                case "ra":
                case "ram":
                    type = "audio/x-pn-realaudio";
                    break;
                case "db":
                    type = "db";
                    break;
                case "sqlite":
                    type = "sqlite";
                    break;

                default:
                    type = "application/octet-stream";
                    break;
            }
        }

        return type;
    }

    public static String getFileSuffix(String name) {
        name = name.substring(name.lastIndexOf('.') + 1, name.length());
        return name.toLowerCase();
    }

    public static final int BUFFER_SIZE = 80 * 1024;

    public interface copyListener {
        void onProgress(int count) throws IOException;
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream,
                                  int bufferSize, copyListener listener)
            throws IOException {
        if (inputStream == null || outputStream == null) {
            throw new IOException();
        }

        byte[] buffer;
        if (bufferSize <= BUFFER_SIZE) {
            bufferSize = BUFFER_SIZE;
        }
        buffer = new byte[bufferSize];

        int count;
        do {

            count = inputStream.read(buffer, 0, bufferSize);
            if (count != -1) {
                outputStream.write(buffer, 0, count);

                if (listener != null) {
                    listener.onProgress(count);
                }
            }
        }
        while (count != -1);

        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    public static String getCloudThumbName(String path, int dataId, int accountId, String suffix) {
        String tmpName = path;
        if (tmpName.contains(".")) {
            tmpName = tmpName.substring(0, tmpName.lastIndexOf("."));
        }
        tmpName = tmpName.replace("/", "@");

        return SecurityUtils.md5String(String.valueOf(dataId + accountId))
                + tmpName + suffix;
    }

    public static String getParentPath(String path) {
        if (path == null || !path.contains("/")) {
            return null;
        }

        String parentPath = path.substring(0, path.lastIndexOf("/"));
        if (parentPath.equals("")) {
            return "/";
        }

        return parentPath;
    }

    public static String getFileName(String path) {
        if (path == null) {
            return null;
        }

        if (!path.contains("/") || path.equals("/")) {
            return path;
        }

        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getDownFileName(String path) {
        if (path == null || !path.contains("/")) {
            return null;
        }

        path = path.substring(path.lastIndexOf("/") + 1);
        if (path.contains("?")) {
            path = path.substring(0, path.lastIndexOf("?"));
        }
        return path;

    }

    public static String concatPath(String path) {
        String lastChar = path.substring(path.length() - 1);
        if (!lastChar.equals(File.separator)) {
            path = path.concat(File.separator);
        }
        return path;
    }

    public static String assemblyPath(String path, String childName) {
        return concatPath(path).concat(childName);
    }

    public static String isFileExist2(String absolutePath) {
        File file = new File(absolutePath);
        if (file.exists()) {
            String parentPath = file.getParent();
            String fileFront;
            String fileExtension = "";

            String fileName = file.getName();
            if (fileName.contains(".")) {
                fileFront = fileName.substring(0, fileName.lastIndexOf("."));
                fileExtension = fileName.substring(fileName.lastIndexOf("."),
                        fileName.length());
            } else {
                fileFront = fileName;
            }

            absolutePath = changNames(parentPath, fileFront, fileExtension);
        }
        return absolutePath;
    }

    /**
     * 判断文件是否存在
     *
     * @param path 路径
     * @return
     */
    public static boolean isFileExist(String path) {
        return !TextUtils.isEmpty(path) && (new File(path)).exists();
    }

    public static final String FILENAME_SEPARATOR_START = "(";
    public static final String FILENAME_SEPARATOR_END = ")";

    public static String changNames(String parentPath, String fileFront, String extension) {

        if (fileFront.contains(FILENAME_SEPARATOR_START) && fileFront.contains(FILENAME_SEPARATOR_END)) {
            fileFront = fileFront.substring(0, fileFront.lastIndexOf(FILENAME_SEPARATOR_START));
        }

        String absolutePath;
        String fileName = fileFront + FILENAME_SEPARATOR_START;
        extension = FILENAME_SEPARATOR_END + extension;

        int i = 1;
        absolutePath = parentPath + File.separator + fileName + i + extension;
        File file = new File(absolutePath);
        while (file.exists()) {
            i++;
            absolutePath = parentPath + File.separator + fileName + i + extension;
            if (!new File(absolutePath).exists()) {
                break;
            }
        }
        return absolutePath;
    }

    public static boolean setSystemWallpaper(InputStream inputStream, Activity activity) {
        if (inputStream == null || activity == null) {
            return false;
        }

        try {
            Bitmap b = BitmapFactory.decodeStream(inputStream);

            Display localDisplay = activity.getWindowManager().getDefaultDisplay();
            float bWidth = b.getWidth();
            int bHeight = b.getHeight();
            float dWidth = localDisplay.getWidth() * 2;
            // float f2 = i;
            float f3 = dWidth / bWidth;
            int k = (int) (bHeight * f3);
            int m = localDisplay.getWidth() * 2;
            Bitmap localBitmap2 = resizeImage(b, m, k);

            activity.getApplicationContext().setWallpaper(localBitmap2);
            b.recycle();
            localBitmap2.recycle();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Bitmap resizeImage(Bitmap paramBitmap, int paramInt1, int paramInt2) {
        int width = paramBitmap.getWidth();
        int height = paramBitmap.getHeight();
        float sx = (float) paramInt1 / (float) width;
        float sy = (float) paramInt2 / (float) height;
        Matrix localMatrix = new Matrix();
        localMatrix.postScale(sx, sy);
        return Bitmap.createBitmap(paramBitmap, 0, 0, width, height, localMatrix, true);
    }

    public static void deleteFile(String path) {
        File file = new File(path);

        if (file.isDirectory()) {

            File[] children = file.listFiles();
            for (File child : children) {
                if (child.isDirectory()) {
                    deleteFile(child.getPath());
                } else {
                    child.delete();
                }
            }

        }

        file.delete();
    }

    private static final int bufferSize = 81920;

    public static void copyFile(String srcPath, String desPath) {
        FileInputStream inFile;
        FileOutputStream outFile;

        try {
            File srcFile = new File(srcPath);
            File desFile = new File(desPath);
            inFile = new FileInputStream(srcFile);
            outFile = new FileOutputStream(desFile);

            byte[] buffer = new byte[bufferSize];
            int count = 0;
            do {
                try {
                    count = inFile.read(buffer, 0, bufferSize);
                    if (count != -1) {
                        outFile.write(buffer, 0, count);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (count != -1);

            inFile.close();
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSignatureMd5(PackageManager pm, String pkg) {
        PackageInfo pi;

        try {
            pi = pm.getPackageInfo(pkg, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }

        Signature[] sig = pi.signatures;
        if (sig == null || sig.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Signature aSig : sig) {
            sb.append(aSig.toCharsString());
        }

        return SecurityUtils.computeMd5(sb.toString());
    }

    public static void donateViaWeb(Context context) {
        try {
            Intent donatei = new Intent(Intent.ACTION_VIEW);
            if (Utils.getDisChannelByAsset(context).equals("amazon")) {
                donatei.setData(Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.geeksoft.filexpert.donate"));
            } else if (Utils.getDisChannelByAsset(context).equals("tstore")) {
                donatei.setData(Uri.parse("http://tsto.re/0000319778"));
            } else {
                donatei.setData(Uri.parse("market://details?id=com.geeksoft.filexpert.donate"));
            }
            context.startActivity(donatei);
        } catch (Exception e) {
        }
    }

    /**
     * 使用迭代的方式添加一个文件夹及它所有的子文件至一个队列中
     *
     * @param rootFile           根文件
     * @param qualifiedFileQueue 并发队列
     * @param fileLists          包含根文件及它所有的子文件的队列
     */
    public static void addFiles(File rootFile, ConcurrentLinkedQueue<File> qualifiedFileQueue,
                                List<File> fileLists) {
        if (rootFile == null || qualifiedFileQueue == null || fileLists == null) {
            return;
        }

        if (rootFile.exists()) {
            File[] files = rootFile.listFiles();
            Collections.addAll(qualifiedFileQueue, files);

            fileLists.add(rootFile);

            while (!qualifiedFileQueue.isEmpty()) {
                File childFile = qualifiedFileQueue.poll();
                if (childFile == null) {
                    continue;
                }

                if (childFile.isFile()) {
                    fileLists.add(childFile);
                    continue;
                }

                File[] childrenFiles = childFile.listFiles();
                if (null == childrenFiles || childrenFiles.length == 0) {
                    fileLists.add(childFile);
                    continue;
                }

                fileLists.add(childFile);
                Collections.addAll(qualifiedFileQueue, childrenFiles);
            }
        }
    }

    public static ArrayMap<String, String> suffixMap = new ArrayMap<String, String>() {
        {
            put("application/rar", ".rar");
            put("x-rar-compressed", ".rar");
            put("application/zip", ".zip");
            put("application/x-7z-compressed", ".7z");
            put("application/x-tar", ".tar");
            put("application/java-archive", ".jar");
            put("text/plain", ".txt");
            put("text/html", ".html");
            put("image/jpeg", ".jpeg");
            put("application/pdf", ".pdf");
        }
    };

    public static String saveAttachment(Context context, Intent intent) {
        int bufferSize = 4096;
        byte[] buffer = new byte[bufferSize];
        String tmpFileName = TimeUtils.getCurrentSecondString() + suffixMap.get(intent.getType());
        String tmpFilePath = TmpFolderUtils.getAttachmentsFile(tmpFileName).getPath();

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(intent.getData());
            if (inputStream == null) {
                return null;
            }

            OutputStream outputStream = new FileOutputStream(tmpFilePath);

            int count = 0;
            do {
                try {
                    count = inputStream.read(buffer, 0, bufferSize);
                    if (count != -1) {
                        outputStream.write(buffer, 0, count);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (count != -1);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            tmpFilePath = null;
        }

        return tmpFilePath;
    }

    public static String convertStreamToString(InputStream is, String encoding)
            throws IOException {
        return convertStreamToString(is, encoding, "\n");
    }

    public static String convertStreamToString(InputStream is, String encoding, String enterChar) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = null;
            InputStreamReader inReader;
            try {
                inReader = new InputStreamReader(is, encoding);
            } catch (UnsupportedEncodingException e) {
                inReader = new InputStreamReader(is, "UTF-8");
            }

            try {
                reader = new BufferedReader(inReader);
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(enterChar);
                }
            } finally {
                closeCloseable(is, reader, inReader);
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static void closeCloseable(java.io.Closeable... c) {
        if (c == null || c.length == 0) {
            return;
        }

        for (int i = c.length - 1; i > -1; i--) {
            closeIO(c[i]);
        }
    }

    public static void closeIO(java.io.Closeable c) {
        if (c == null) {
            return;
        }

        try {
            c.close();
        } catch (java.io.IOException e) {
        }
    }

    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String getFormatSize(long size) {
        String[] unit = new String[]{"MB", "GB", "TB"};
        int index = 0;
        while (size >= 1024) {
            size = size / 1024;
            index++;
        }

        return size + unit[index];
    }

    public static byte[] inputStream2byte(InputStream inputStream) {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int byteBuff = 8192;
        byte[] buff = new byte[byteBuff];
        int rc;
        try {
            while ((rc = inputStream.read(buff, 0, byteBuff)) > 0) {
                swapStream.write(buff, 0, rc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return swapStream.toByteArray();
    }

    private static final int CAPACITY = 1024 * 8;

    public static boolean fileNameSinaCloud(String str){
        if (TextUtils.isEmpty(str) || str.length() < 6 || str.length() > 63){
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-z]+[\\-a-z0-9]+[a-z0-9]$");
        Matcher m = pattern.matcher(str);
        return m.find();
    }

    public static void shredderFile(FileInfo file, long size) throws IOException {
        if (!file.exists()) {
            file.create(false);
        }

        OutputStream outputStream = file.getOutputStream();
        Random r = new Random();
        StringBuilder sb = new StringBuilder(CAPACITY);
        long number = size % (CAPACITY);
        long count = size / (CAPACITY);
        String data;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < CAPACITY / 8; j++) {
                sb.append(String.valueOf(r.nextInt(Integer.MAX_VALUE)));
            }

            data = sb.toString();
            outputStream.write(data.getBytes());
            outputStream.flush();
            sb.delete(0, data.length());

        }

        if (number > 0) {
            for (int j = 0; j < CAPACITY / 8; j++) {
                sb.append(String.valueOf(r.nextInt(Integer.MAX_VALUE)));
            }

            data = sb.toString();
            outputStream.write(data.getBytes());
            outputStream.flush();
            sb.delete(0, data.length());

        }

        outputStream.close();
    }
}
