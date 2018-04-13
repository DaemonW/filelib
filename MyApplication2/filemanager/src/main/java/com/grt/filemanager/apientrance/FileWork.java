package com.grt.filemanager.apientrance;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.BaseData;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.MediaUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by LDC on 2018/3/16.
 */

public class FileWork {
    public static final String SRC_PATH = "src_path";
    public static final String DES_FOLDER_PATH = "des_folder_path";
    public static final String CREATE_FOLDER = "create_folder";
    public static final String IS_CONFLICT = "is_conflict";
    public static final String SRC_DATA_ID = "src_dataId";
    public static final String DES_DATA_ID = "des_dataId";
    public static boolean deleteFolder(FileInfo file, int dataId, DataModel desData) {
        boolean result = false;
        if (!file.isFolder()) {
            return deleteFile(file, dataId, desData);
        }

        ConcurrentLinkedQueue<FileInfo> qualifiedFileQueue = new ConcurrentLinkedQueue<>();
        List<FileInfo> fileLists = new ArrayList<>();
        fileLists = BaseData.addFiles(file, qualifiedFileQueue, fileLists);
        FileInfo currentFile;
        int count = fileLists.size();
        for (int i = count - 1; 0 <= i; i--) {
            currentFile = fileLists.get(i);
            result = deleteFile(currentFile, dataId, desData);
        }

        return result;
    }

    public static boolean deleteFile(FileInfo file, int dataId, DataModel mDesData) {

        boolean result = deleteFileImpl(file);

        if (result){
            switch (dataId) {
                case DataConstant.APP_DATA_ID:
                    mDesData.deleteDbCache(file.getSpecialInfo(DataConstant.SOURCE_PATH)
                            .getString(DataConstant.SOURCE_PATH), file.isFolder());
                    break;

                case DataConstant.DOC_DATA_ID:
                    mDesData.deleteDbCache(file.getPath(), file.isFolder());
                    break;

                default:
                    mDesData.deleteDbCache(file.getPath(), file.isFolder());
                    break;
            }
        }
        return result;
    }

    private static boolean deleteFileImpl(FileInfo file) {
        if (file.isFolder()) {
            return file.delete();
        } else {
            return deleteCatch(file);
        }
    }

    private static boolean deleteCatch(FileInfo file) {
        long size = file.getSize();
        if (size != 0) {
            return writeTxt(file, size);
        } else {
            return file.delete();
        }
    }

    private static boolean writeTxt(FileInfo file, long size) {
        try {
            FileUtils.shredderFile(file, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.delete();
    }

    /**
     * 检查是否有权限在当前目录复制
     * @param curData 包含当前目的地文件信息的源数据
     * @return 有权限返回true，反之则返回false.
     */
    public static boolean checkOperationPermission(DataModel curData) {
        String operationPermission = curData.getCurrentFolder()
                .getSpecialInfo(DataConstant.OPERATION_PERMISSION)
                .getString(DataConstant.OPERATION_PERMISSION, DataConstant.ALL_ALLOW_PERMISSION);

        return DataConstant.canCopyTo(operationPermission);
    }

    /**
     * 检查该路径文件列表是否包含冲突的文件
     * @param desDataId 目的地数据类型Id
     * @param srcDataId 源数据类型Id
     * @param selectedList 待粘贴的文件列表
     * @param desFolderPath 目的地路径
     * @param curChildren 目的地路径下文件列表
     * @return
     */
    public static int checkConflictName(int desDataId, int srcDataId, List<FileInfo> selectedList,
                                        String desFolderPath, List<FileInfo> curChildren){

        if (DataConstant.isNetId(desDataId) && srcDataId != desDataId) {
            if (DataConstant.isNetId(srcDataId)) {
                return ResultConstant.DEVELOPING;
            }
        }

        if (DataConstant.hasFileId(desDataId)) {

            if (desDataId == DataConstant.MY_CLOUD_ID) {
                return checkForGCloud(selectedList);
            }

            return ResultConstant.SUCCESS;
        }

        ArrayList<FileInfo> newConflictFiles = new ArrayList<>();
        ArrayList<FileInfo> oldConflictFiles = new ArrayList<>();

        FileInfo firstSelectFile = selectedList.get(0);
        boolean isConflict = false;
        FileInfo child;
        String curFileName;
        int count = curChildren.size();
        ArrayList<Integer> conflictIndexList = new ArrayList<>();
        ArrayList<String> mChildrenNames = new ArrayList<>();
        ArrayMap<String, Integer> mPositionMap = new ArrayMap<>();
        for (int index = 0; index < count; index++) {
            child = curChildren.get(index);
            curFileName = child.getName().toLowerCase();
            mChildrenNames.add(curFileName);
            mPositionMap.put(curFileName, index);

            if (checkCopyParentToChild(srcDataId, desDataId,
                    desFolderPath, firstSelectFile) == ResultConstant.FORBIDDEN) {
                return ResultConstant.FORBIDDEN;
            }

            if (firstSelectFile.getName().toLowerCase().equals(curFileName)) {
                newConflictFiles.add(firstSelectFile);
                oldConflictFiles.add(child);
                conflictIndexList.add(0);
                isConflict = true;
            }
        }

        FileInfo file;
        String fileName;
        FileInfo conflict;
        count = selectedList.size();
        for (int index = 1; index < count; index++) {
            file = selectedList.get(index);
            fileName = file.getName().toLowerCase();

            if (checkCopyParentToChild(srcDataId, desDataId,
                    desFolderPath, file) == ResultConstant.FORBIDDEN) {
                return ResultConstant.FORBIDDEN;
            }

            if (mChildrenNames.contains(fileName)) {
                conflict = curChildren.get(mPositionMap.get(fileName));
                newConflictFiles.add(file);
                oldConflictFiles.add(conflict);
                conflictIndexList.add(index);
                isConflict = true;
            }
        }

        if (isConflict) {

            for (int index = conflictIndexList.size() - 1; index >= 0; index--) {
                selectedList.remove(conflictIndexList.get(index).intValue());
            }
        }

        return isConflict ? ResultConstant.EXIST : ResultConstant.SUCCESS;
    }

    private static int checkForGCloud(List<FileInfo> selectedList) {
        int count = selectedList.size();
        if (count == 1) {
            if (selectedList.get(0).getSize() <= 0) {
                return ResultConstant.FORBIDDEN_UPLOAD_0_FILE;
            }
        }

        for (FileInfo file : selectedList) {
            if (file.isFolder()) {
                return ResultConstant.DEVELOPING;
            }
        }

        return ResultConstant.SUCCESS;
    }

    private static int checkCopyParentToChild(int srcDataId, int desDataId,
                                       String desFolderPath, FileInfo child) {
        switch (srcDataId) {
            case DataConstant.VIDEO_DATA_ID:
            case DataConstant.IMAGE_DATA_ID:
            case DataConstant.MIX_CLOUD_ID:
                if (srcDataId != desDataId) {
                    return ResultConstant.SUCCESS;
                }
                break;

            default:
                break;
        }

        return desFolderPath.contains(child.getPath()) ?
                ResultConstant.FORBIDDEN : ResultConstant.SUCCESS;
    }

    public static Integer copyWork(Bundle args, DataModel desData, DataModel srcData){
        String mSrcPath = args.getString(SRC_PATH);
        String mDesFolderPath = args.getString(DES_FOLDER_PATH);
        args.getBoolean(CREATE_FOLDER);
        boolean isConflact = args.getBoolean(IS_CONFLICT);
        int srcDataID = args.getInt(SRC_DATA_ID);
        int desDataID = args.getInt(DES_DATA_ID);
        int result;

//        mSrcData = DataFactory.getData(mProgressData.getSrcFragmentId());
//        mDesData = DataFactory.getData(mProgressData.getDesFragmentId());

        FileInfo srcFile = getSrcFile(srcData, mSrcPath);
        FileInfo desFile = getDesFile(srcFile, desData, srcDataID, mDesFolderPath);

        if (srcFile.isFolder()) {
            result = copyFolder(srcFile, desFile, true, desData, desDataID, srcDataID, isConflact);
        } else {
            result = copyFile(srcFile, desFile, isConflact, desData, desDataID);
        }

        return result;
    }

    private static FileInfo getDesFile(FileInfo srcFile, DataModel desData, int srcDataId, String mDesFolderPath) {
        FileInfo desFile = desData.getFileInfo(
                FileUtils.concatPath(mDesFolderPath).concat(
                        getDesFileName(srcDataId, srcFile)));
        return desFile;
    }

    public static String getDesFileName(int srcDataId, FileInfo file) {
        String name = file.getName();
        if (srcDataId == DataConstant.APP_DATA_ID) {
            if (!name.endsWith(DataConstant.APK_SUFFIX)) {
                name = name.concat(DataConstant.APK_SUFFIX);
            }
        }

        return name;
    }

    private static int copyFolder(FileInfo srcFile, FileInfo desFile, boolean mCreateFolder, DataModel desData, int desDataID,
                                  int srcDataId, boolean isConflact) {
        int result = ResultConstant.SUCCESS;
        if (mCreateFolder) {
            result = createFile(srcFile, desFile, desData, desDataID);
        }

        if (result == ResultConstant.SUCCESS || result == ResultConstant.EXIST) {

            List<FileInfo> children = srcFile.getList(true);
            for (FileInfo child : children) {
                FileInfo desChildFile = getDesChildFile(child, desFile, desData);

                if (child.isFolder()) {

                    result = createFile(child, desChildFile, desData, desDataID);

                    if (result == ResultConstant.SUCCESS || result == ResultConstant.EXIST) {
                        startCopyFolderWork(child, desChildFile, false, srcDataId, desDataID);
                    } else {
                        desData.stopDBCacheService();
                    }

                } else {
                    copyFile(child, desChildFile, isConflact, desData, desDataID);
                }
            }

        } else {
//            mProgressData.addFailed(srcFile);
//            mProgressData.setOperationStatus(ProgressData.STATUS_FAIL);
            desData.stopDBCacheService();
            return ResultConstant.FAILED;
        }

        return result;
    }

    private static int createFile(FileInfo srcFile, FileInfo desFile, DataModel dataModel, int desDataId) {
        int result = ResultConstant.SUCCESS;
        boolean isFolder = srcFile.isFolder();

        if (!desFile.exists()) {
            result = desFile.create(isFolder);
        }

        if (isFolder) {
            finishCopyFile(result, srcFile, desFile, desDataId, dataModel);
        }

        return result;
    }

    private static void finishCopyFile(int result, FileInfo srcFile, FileInfo desFile, int desDataId, DataModel desData) {
        if (result == ResultConstant.SUCCESS) {
            if(desDataId == DataConstant.SAFEBOX_ID) {
                desData.addDbCache(desFile, srcFile.getPath(), false);
            } else {
                desData.addDbCache(desFile);
            }
            desData.stopDBCacheService();
        }
    }

    private static FileInfo getDesChildFile(FileInfo srcFile, FileInfo desFolderFile, DataModel mDesData) {
        FileInfo desFile;

//        if (mDesUniqueType == ProgressData.UNIQUE_TYPE_ID) {
//            desFile = ((IdModel) mDesData).createFileInfo(srcFile.getName(), srcFile.isFolder(),
//                    desFolderFile.getSpecialInfo(DataConstant.FILE_ID).getString(DataConstant.FILE_ID));
//        } else {
            desFile = mDesData.getFileInfo(FileUtils.concatPath(desFolderFile.getPath())
                    .concat(srcFile.getName()));
//        }

        return desFile;
    }

    protected static void startCopyFolderWork(FileInfo srcFile, FileInfo desFile, boolean createFolder, int srcDataId, int desDataId) {
        String srcPath = srcFile.getPath();

        setArgs(srcPath, FileUtils.getParentPath(desFile.getPath()), createFolder, false, srcDataId, desDataId);
    }

    public static Bundle setArgs(String srcPath, String desFolderPath,
                                  boolean createFolder, boolean isConflict, int srcDataID, int desDataID) {
        Bundle args = new Bundle();
        args.putString(SRC_PATH,srcPath );
        args.putString(DES_FOLDER_PATH,desFolderPath );
        args.getBoolean(CREATE_FOLDER);
        args.putBoolean(CREATE_FOLDER, createFolder);
        args.putBoolean(IS_CONFLICT, isConflict);
        args.putInt(SRC_DATA_ID, srcDataID);
        args.putInt(DES_DATA_ID, desDataID);
        return args;

    }

    protected static int copyFile(FileInfo srcFile, FileInfo desFile, boolean isConflact, DataModel desData, int desDataId) {

        int result = copyFileImpl(srcFile, desFile, isConflact, desData, desDataId);

        finishCopyFile(result, srcFile, desFile, desDataId, desData);

        return result;
    }

    protected static int copyFileImpl(FileInfo srcFile, FileInfo desFile, boolean mIsConflict, DataModel desData, int desDataId) {
        if (!srcFile.exists()) {
            return ResultConstant.FAILED;
        }

        String desPath = desFile.getPath();
        if (!srcFile.getPath().equals(desPath)) {
            if (mIsConflict) {
//                List<FileInfo> OldConflicts = mProgressData.getOldConflictList();
//                for (FileInfo cur : OldConflicts) {
//                    String curPath = cur.getPath();
//                    if (curPath.toLowerCase().equals(desPath.toLowerCase())) {
//                        desPath = curPath;
//                        cur.delete();
//                        break;
//                    }
//                }
                desData.deleteDbCache(desPath, desFile.isFolder());
                MediaUtils.directDelMediaStore(desPath);
            }

            if (ResultConstant.FAILED == createFile(srcFile, desFile, desData, desDataId)) {
                return ResultConstant.FAILED;
            }
        } else {
            return ResultConstant.SUCCESS;
        }

        try {
            FileUtils.copyStream(srcFile.getInputStream(), desFile.getOutputStream(), 0,
                    new FileUtils.copyListener() {
                        @Override
                        public void onProgress(int count) throws IOException {
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            if (!mIsConflict) {
                desFile.delete();
            }
            return ResultConstant.FAILED;
        }

        return ResultConstant.SUCCESS;
    }

    private static FileInfo getSrcFile(DataModel mSrcData, String mSrcPath) {
        FileInfo srcFile;

//        mSrcUniqueType = mProgressData.getSrcUniqueType();
//        if (mSrcUniqueType == ProgressData.UNIQUE_TYPE_ID) {
//            srcFile = ((IdModel) mSrcData).getFileInfoById(mSrcPath);
//        } else {
            srcFile = mSrcData.getFileInfo(mSrcPath);
//        }

        return srcFile;
    }

}
