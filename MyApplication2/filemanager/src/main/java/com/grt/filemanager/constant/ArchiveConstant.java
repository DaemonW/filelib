package com.grt.filemanager.constant;


import android.text.TextUtils;

import com.grt.filemanager.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class ArchiveConstant {

    private ArchiveConstant() {
    }

    public final static String RAR_SUFFIX = ".rar";
    public final static String ZIP_SUFFIX = ".zip";
    public final static String SEVEN_Z_SUFFIX = ".7z";
    public final static String TAR_SUFFIX = ".tar";
    public final static String JAR_SUFFIX = ".jar";
    public final static String APK_SUFFIX = ".apk";
    public final static String NO_PASSWORD = "";
    public final static String REAL_PATH = "real_path";

    public final static String COMPRESS_LEVEL = "compress_level";
    public final static int COMPRESS_LEVEL_FASTEST = 0;
    public final static int COMPRESS_LEVEL_FAST = 1;
    public final static int COMPRESS_LEVEL_NORMAL = 2;
    public final static int COMPRESS_LEVEL_MAX = 3;
    public final static int COMPRESS_LEVEL_ULTRA = 4;
    public final static int COMPRESS_FORMAT_NORMAL = 0;


    private static ArrayList<String> suffixList = new ArrayList<String>() {
        {
            add(RAR_SUFFIX);
            add(ZIP_SUFFIX);
            add(SEVEN_Z_SUFFIX);
            add(TAR_SUFFIX);
            add(JAR_SUFFIX);
            add(APK_SUFFIX);
        }
    };

    private static ArrayList<String> supportEncodeList = new ArrayList<String>() {
        {
            add(ZIP_SUFFIX);
        }
    };

    public static boolean getSupportEncodeList(String path) {
        for (String suffix : supportEncodeList) {
            if (path.contains(suffix)) {
                return true;
            }
        }

        return false;
    }

    private static ArrayList<String> successRecord = new ArrayList<>();

    public static String getArchiveFilePath(String filePath) {
        String archiveFilePath = filePath;
        String suffix = getArchiveSuffix(filePath);
        if (!filePath.endsWith(suffix)) {
            try {
                archiveFilePath = filePath.substring(0, filePath.lastIndexOf(suffix) + suffix.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return archiveFilePath;
    }

    public static String getArchiveSuffix(String filePath) {
        for (String suffix : suffixList) {
            if (filePath.contains(suffix)) {
                return suffix;
            }
        }

        return ZIP_SUFFIX;
    }

    public static boolean isInArchive(String path) {
        return !new File(path).isDirectory() && isContainsArchiveSuffix(path);
    }

    public static boolean isEndArchiveSuffix(String path) {
        if (path != null) {
            for (String suffix : suffixList) {
                if (path.endsWith(suffix)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isContainsArchiveSuffix(String path) {
        if (path != null) {
            for (String suffix : suffixList) {
                if (path.contains(suffix)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isArchiveFile(String path) {
        if (path != null) {
            String suffix = ".".concat(FileUtils.getFileSuffix(path));
            if (supportArchive(suffix)) {

                String parentPath = FileUtils.getParentPath(path);
                suffix = ".".concat(FileUtils.getFileSuffix(parentPath));
                if (!supportArchive(suffix)) {
                    return true;
                }

            }
        }

        return false;
    }

    public static boolean supportArchive(String suffix) {
        return suffixList.contains(suffix);
    }

    public static boolean supportEncodeArchive(String suffix) {
        return supportEncodeList.contains(suffix);
    }

    public static String parserZipFileName(String fileName) {
        if (fileName.contains("/")) {
            if (fileName.substring(fileName.length() - 1).equals("/")) {
                fileName = fileName.substring(0, fileName.lastIndexOf("/"));
                if (fileName.contains("/")) {
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                }
            } else {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }
        }

        return fileName;
    }

    public static void deleteSuccessRecord() {
        for (String path : successRecord) {
            FileUtils.deleteFile(path);
        }

        successRecord.clear();
    }

    public static void clearSuccessRecord() {
        successRecord.clear();
    }

    public static void addSuccessRecord(String path) {
        if (successRecord == null) {
            successRecord = new ArrayList<>();
        }

        if (!TextUtils.isEmpty(path)) {
            successRecord.add(path);
        }
    }

    /**
     * 创建文件路径中不存在的文件夹，并且在内存中记录，用于取消操作
     *
     * @param desDirPath 解压到的文件夹
     * @param filePath   解压的文件路径
     */
    public static void createParentDir(String desDirPath, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            successRecord.add(filePath);
        }

        String parentDirPath = FileUtils.getParentPath(filePath);
        File parentDir = new File(parentDirPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
            successRecord.add(parentDirPath);

            String[] needCreateDir = filePath.substring(desDirPath.length() + 1).split("/");
            parentDirPath = FileUtils.concatPath(desDirPath) + needCreateDir[0];
            successRecord.add(parentDirPath);
        }
    }

    public static String getCompressionFilePath(String filePath) {
        String compressionFilePath = filePath;
        String suffix = getArchiveSuffix(filePath);

        if (!filePath.endsWith(suffix)) {
            try {
                compressionFilePath = filePath.substring(0, filePath.lastIndexOf(suffix) + suffix.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return compressionFilePath;
    }

    public static String getCompressionFileInsidePath(String filePath) {
        String tmpFilePath = filePath;

        String suffix = getArchiveSuffix(filePath);

        if (filePath.contains(suffix)) {
            tmpFilePath = filePath.substring(filePath.lastIndexOf(suffix) + suffix.length() + 1);
        }

        return tmpFilePath;
    }

    public static String createExtractDesFolder(String archiveFilePath, String name) {
        String desFolderPath = getCompressionFilePath(archiveFilePath);
        desFolderPath = FileUtils.getParentPath(desFolderPath);
        String newFolderName = name.contains(".") ? name.substring(0, name.lastIndexOf(".")) : name;
        return desFolderPath + "/" + newFolderName;
    }

}
