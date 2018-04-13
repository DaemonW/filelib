package com.grt.filemanager.util;

import android.os.Environment;
import android.text.TextUtils;


import com.grt.filemanager.apientrance.ApiPresenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class TmpFolderUtils {

    private TmpFolderUtils() {
    }

    public static final String TEMP_FOLDER_NAME = ".ManagerStorage";
    public static final String RECEIVED_FOLDER_NAME = "ReceivedFiles";
    public static final String DOWNLOAD_FOLDER_NAME = "download";
    public static final String DOWNLOAD_TEMP_FOLDER_NAME = ".TempDown";
    public static final String HIDDEN_FILE_NAME = ".nomedia";
    public static final String loadImage = "cache";
    public static final String ATTACHMENTS = ".attachments";
    public static final String DEL_TEMP = "Temp";
    public static final String RING_TONES = "/Ringtones";
    public static final String HTML = "/Html";
    public static final String PICTURES = "/Pictures";

    //New path
    private static final String safeboxDir = "/.box";
    public static final String PLUGIN_THUMB_DIR = ".pluginThumb";
    public static final String PLUGIN_TMP_DIR = ".pluginTmp";
    private static final String safeTempDir = ".tempCache";

    public static File getTmpFile(String fileName) {
        String tempDirPath = getTempDirPath().concat(PLUGIN_TMP_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File tmpFile = new File(tempDirPath + File.separator + fileName);
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tmpFile;
    }


    public static File getThumbFile(String fileName) {
        String tempDirPath = getTempDirPath().concat("/").concat(PLUGIN_THUMB_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File tmpFile = new File(tempDirPath + File.separator + fileName);
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tmpFile;
    }

    public static File getTempFile(String folder, String fileName) {
        String tempDirPath = getTempDirPath().concat("/").concat(PLUGIN_THUMB_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        tempDirPath = tempDirPath.concat("/").concat(folder);
        tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File tmpFile = new File(tempDirPath.concat("/").concat(fileName));
        if (!tmpFile.exists()) {
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return tmpFile;
    }

    public static File getTempFolder(String folder) {
        String tempDirPath = getTempDirPath().concat("/").concat(PLUGIN_THUMB_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        tempDirPath = tempDirPath.concat("/").concat(folder);
        tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        return tmpDir;
    }

    public static File getAttachmentsFile(String fileName) {
        return getTempFile(ATTACHMENTS, fileName);
    }

    public static String getSafeBoxDir() {
        String tempDir = getTempDirPath();
        if (TextUtils.isEmpty(tempDir)) return null;

        tempDir = tempDir.concat(safeboxDir);
        File file = new File(tempDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return tempDir;
    }

    public static String getSafeTempDir() {
        String tempDir = getTempDirPath();
        if (TextUtils.isEmpty(tempDir)) return null;

        tempDir = tempDir + File.separator + safeTempDir;
        File file = new File(tempDir);
        if (!file.exists()) {
            file.mkdir();
        }
        return tempDir;
    }

    public static String getTempDirPath() {
        String tempPath = Environment.getExternalStorageDirectory().getPath()
                + File.separator + TEMP_FOLDER_NAME;
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return tempPath;
    }


    /**
     * @return 返回备份Apk文件的路径
     */
    public static String getBackupPath() {
        return Environment.getExternalStorageDirectory()
                .getPath() + "/backup_apps";
    }


    /**
     * @return 返回下载器保存文件的路径
     */
    public static String getDownloaderDefaultSavePath() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    public static String getTempDownDirName() {
        return Environment.getExternalStorageDirectory().getPath().concat(DOWNLOAD_TEMP_FOLDER_NAME);
    }

    public static final String COMPRESSION_TEMP_DIR = "/.compressionTempDir";

    public static String getCompressionTempDir(int taskId) {
        String tempDirPath = getTempDirPath().concat(COMPRESSION_TEMP_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        tempDirPath = tempDirPath + File.separator + taskId;
        tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        return tempDirPath;
    }

    private static final String SECRET_TEMP_DIR = "/.s";

    public static String getSecretTempDir(String folder) {
        String tempDirPath = getTempDirPath().concat(SECRET_TEMP_DIR);
        File tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        tempDirPath = tempDirPath + File.separator + folder;
        tmpDir = new File(tempDirPath);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        return tempDirPath;
    }

    public static void writeLog(String key, String logInfo) {
        try {
            if (Utils.getDisChannelByAsset(ApiPresenter.getContext()).equals("test")) {
                String cachePath = getTempDirPath() + File.separator + loadImage;
                File dir = new File(cachePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(cachePath, "logInfo.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }

                if (file.exists()) {
                    FileWriter writer = new FileWriter(file, true);
                    writer.write("\n\n" + key + "=" + logInfo + "\n\n");
                    writer.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeUserHelpLog(String key, String logInfo) {
        try {
            String cachePath = Environment.getExternalStorageDirectory().getPath() + File.separator + DOWNLOAD_FOLDER_NAME;
            File dir = new File(cachePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(cachePath, "logInfo.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            if (file.exists()) {
                FileWriter writer = new FileWriter(file, true);
                writer.write("\n\n" + key + "=" + logInfo + "\n\n");
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTempFilePath() {
        return TmpFolderUtils.getSafeTempDir() + File.separator + System.currentTimeMillis() + ".dat";
    }

    public static String getTempMiddleFilePath() {
        String dir = TmpFolderUtils.getSafeTempDir() + File.separator + "middle";
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }

        return dir + File.separator + System.currentTimeMillis() + ".dat";
    }

}
