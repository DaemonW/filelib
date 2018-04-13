package com.grt.filemanager.model.imp.local.archive;

import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.model.ArchiveFileInfo;

import java.io.File;

public class ArchiveFactory {

    private ArchiveFactory() {
    }

    private static ArrayMap<String, ArchiveFileInfo> fileArray = new ArrayMap<>();

    public static ArchiveFileInfo getArchiveFile(String path, String password) {
        return getArchiveFile(path, password, true);
    }

    /**
     * @param path     压缩文件路径
     * @param password 压缩文件密码
     * @return 文件信息接口
     */
    public static ArchiveFileInfo getArchiveFile(String path, String password, boolean createNew) {
        ArchiveFileInfo file;
        if (!createNew) {
            file = fileArray.get(path);
            if (file != null) {
                return file;
            }
        }

        String suffix = ArchiveConstant.getArchiveSuffix(path);
        switch (suffix) {
            case ArchiveConstant.RAR_SUFFIX:
                file = new RarObject(path, password);
                break;

            case ArchiveConstant.SEVEN_Z_SUFFIX:
                file = new SevenZObject(path, password);
                break;

            case ArchiveConstant.ZIP_SUFFIX:
                file = new ZipObject(path, password);
                break;

            case ArchiveConstant.TAR_SUFFIX:
                file = new TarObject(path, password);
                break;

            case ArchiveConstant.JAR_SUFFIX:
                file = new JarObject(path,password);
                break;

            case ArchiveConstant.APK_SUFFIX:
                file = new ApkObject(path,password);
                break;

            default:
                file = new RarObject(path, password);
                break;
        }

        fileArray.put(path, file);

        return file;
    }

    public static void removeFileCache(String path) {
        fileArray.remove(path);
    }

    public static boolean isEncrypted(String path) {
        String suffix = ArchiveConstant.getArchiveSuffix(path);
        boolean result = false;
        File file = new File(ArchiveConstant.getArchiveFilePath(path));

        switch (suffix) {
            case ArchiveConstant.RAR_SUFFIX:
                result = RarObject.isEncrypted(file);
                break;

            case ArchiveConstant.SEVEN_Z_SUFFIX:
                result = SevenZObject.isEncrypted(file);
                break;

            case ArchiveConstant.ZIP_SUFFIX:
                result = ZipObject.isEncrypted(file);
                break;

            default:
                break;
        }

        return result;
    }

}
