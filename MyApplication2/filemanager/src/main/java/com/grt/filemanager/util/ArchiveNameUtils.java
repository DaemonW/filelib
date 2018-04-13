package com.grt.filemanager.util;

import android.text.TextUtils;


import com.grt.filemanager.model.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArchiveNameUtils {

    private static List<String> existingName = new ArrayList<>();
    private static List<String> newExtractPath = new ArrayList<>();

    private ArchiveNameUtils() {
    }

    public static List<String> getNoDuplicateList(List<FileInfo> fileList, int taskId) {
        boolean hit = false;
        ArrayList<String> newList = new ArrayList<>();
        newList.add(fileList.get(0).getPath());

        FileInfo fileInfo;
        String newPath;
        String newName = null;
        String testFileName;
        String testNewFileName;
        String desPath = null;
        int count = fileList.size();
        for (int index = 1; index < count; index++) {
            fileInfo = fileList.get(index);
            newPath = fileInfo.getPath();
            testFileName = fileInfo.getName();

            for (int indey = 0; indey < newList.size(); indey++) {

                testNewFileName = FileUtils.getFileName(newList.get(indey));
                if (testFileName.equals(testNewFileName)) {
                    String[] names = assembleArrayName(testFileName);

                    newName = getNewOrder(names[0]) + "." + names[1];
                    hit = true;
                    break;
                }
            }

            if (hit) {
                if (TextUtils.isEmpty(desPath)) {
                    desPath = TmpFolderUtils.getCompressionTempDir(taskId);
                }

                newPath = FileUtils.concatPath(desPath).concat(newName);
                FileUtils.copyFile(fileInfo.getPath(), newPath);

                hit = false;
            }

            newList.add(newPath);
        }

        return newList;
    }

    public static String createNoExistingName(String fileName, String parentPath) {
        File testFile;
        int index = 0;
        boolean hasSuffix = fileName.contains(".");

        int suffixIndex;
        String prefix;
        String suffix = "";
        if (hasSuffix) {
            suffixIndex = fileName.lastIndexOf(".");
            prefix = fileName.substring(0, suffixIndex);
            suffix = fileName.substring(suffixIndex);
        } else {
            prefix = fileName;
        }

        while (true) {
            testFile = new File(FileUtils.concatPath(parentPath).concat(fileName));
            if (!testFile.exists() || testFile.isDirectory()) {
                break;
            }

            index++;

            if (hasSuffix) {
                fileName = prefix + "_" + index + suffix;
            } else {
                fileName = prefix + "_" + index;
            }

        }

        return fileName;
    }

    public static void getExistingNameList(String parentPath) {
        if (existingName == null) {
            existingName = new ArrayList<>();
        }

        existingName.clear();
        File parent = new File(parentPath);
        File[] children = parent.listFiles();

        if (children != null) {
            for (File child : children) {
                existingName.add(child.getName());
            }
        }
    }

    public static String getNewOrder(String fileName) {
        String newFileName;

        if (hasIndex(fileName)) {
            int order = getIndex(fileName);

            newFileName = assembleNewIndexName(getNoSuffixName(fileName), order + 1);
        } else {
            newFileName = assembleNewIndexName(fileName, 1);
        }

        return newFileName;
    }

    //已判定srcFileName，desFileName包含.
    public static String[] assembleArrayName(String fileName) {
        String[] names = fileName.split("\\.");
        String[] newName = new String[2];

        int count = names.length;
        if (count <= 2) {
            return names;
        }

        String formerName = names[0] + "." + names[1];
        for (int index = 2; index < count - 1; index++) {
            formerName += "." + names[index];
        }

        newName[0] = formerName;
        newName[1] = names[count - 1];
        return newName;
    }

    /**
     * @param name 不包含后缀的文件名
     * @return 文件名中包含的数字下标
     */
    private static int getIndex(String name) {
        return Integer.valueOf(name.substring(name.lastIndexOf("_") + 1));
    }

    private static String getNoSuffixName(String name) {
        return name.substring(0, (name.lastIndexOf("_")));
    }

    private static boolean hasIndex(String name) {
        return name.lastIndexOf("_") > 0 && isNumeric(name.substring(name.lastIndexOf("_") + 1));
    }

    /**
     * @param name  不包含后缀的文件名
     * @param index 下标值
     * @return 包含数字下标的文件名
     */
    private static String assembleNewIndexName(String name, int index) {
        return name.concat("_").concat(String.valueOf(index));
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static void clearList() {
        existingName.clear();
        if (newExtractPath != null) {
            newExtractPath.clear();
        }
    }

}
