package com.grt.filemanager.model.imp.local.archive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.ArchiveObject;
import com.grt.filemanager.model.CompressListener;
import com.grt.filemanager.model.ExtractListener;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.imp.local.archive.inter.unrar.Archive;
import com.grt.filemanager.model.imp.local.archive.inter.unrar.UnrarCallback;
import com.grt.filemanager.model.imp.local.archive.inter.unrar.exception.RarException;
import com.grt.filemanager.model.imp.local.archive.inter.unrar.rarfile.FileHeader;
import com.grt.filemanager.util.ArchiveNameUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LocalFileHelper;
import com.grt.filemanager.util.TimeUtils;
import com.grt.filemanager.util.TmpFolderUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rar压缩文件实现类
 */
public class RarObject extends ArchiveObject {

    private static ArrayMap<String, Archive> rarArray = new ArrayMap<>();

    private Archive mArchive;
    private File mFile;

    private FileHeader mHeader;
    private String mPath;

    public RarObject(String path, String password) {
        this.mPath = path;

        String archiveFilePath;
        boolean isRarFile = ArchiveConstant.isArchiveFile(path);
        if (!isRarFile) {
            archiveFilePath = ArchiveConstant.getArchiveFilePath(path);
        } else {
            archiveFilePath = path;
            this.mHeader = null;
        }

        this.mArchive = rarArray.get(archiveFilePath);
        if (mArchive == null) {
            try {
                this.mFile = new File(archiveFilePath);
                this.mArchive = new Archive(mFile, password, false);

                rarArray.put(archiveFilePath, mArchive);
            } catch (RarException | IOException e) {
                e.printStackTrace();
            }
        } else {
            this.mFile = mArchive.getFile();
        }

        if (!isRarFile) {
            this.mHeader = getFileHeader(path);
        }
    }

    private FileHeader getFileHeader(String filePath) {
        FileHeader fileHeader = null;

        List<FileHeader> headers = mArchive.getFileHeaders();
        int count = headers.size();
        for (int index = 0; index < count; index++) {
            fileHeader = headers.get(index);
            String fileName = fileHeader.isUnicode() ?
                    fileHeader.getFileNameW() : fileHeader.getFileNameString();

            if (filePath.contains(fileName)) {
                break;
            }
        }

        return fileHeader;
    }

    public RarObject(FileHeader header, String archiveFilePath) {
        this.mArchive = rarArray.get(archiveFilePath);
        this.mFile = mArchive.getFile();
        this.mHeader = header;
        this.mPath = archiveFilePath.concat("/").concat(getInsidePath(header));
    }

    private String getInsidePath(FileHeader header) {
        String name = header.isUnicode() ? header.getFileNameW() : header.getFileNameString();
        if (name.contains("\\")) {
            name = name.replace("\\", "/");
        }
        return name;
    }

    @Override
    public String getName() {
        String name;
        if (mHeader == null) {
            name = mFile.getName();
        } else {
            name = getFileName();
        }

        return name;
    }

    private String getFileName() {
        String name = mHeader.isUnicode() ? mHeader.getFileNameW() : mHeader.getFileNameString();
        if (name.contains("\\")) {
            name = name.substring(name.lastIndexOf("\\") + 1);
        }
        return name;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public long getLastModified() {
        return mHeader == null ? mFile.lastModified() : mHeader.getMTime().getTime();
    }

    @Override
    public long getSize() {
        return mHeader == null ? mFile.length() : mHeader.getFullUnpackSize();
    }

    @Override
    public String getMimeType() {
        if (mHeader != null) {
            if (mHeader.isDirectory()) {
                return DataConstant.MIME_TYPE_FOLDER;
            } else {
                return FileUtils.getMiMeType(mHeader.isUnicode() ?
                        mHeader.getFileNameW() : mHeader.getFileNameString());
            }
        } else {
            return FileUtils.getMiMeType(mFile.getName());
        }
    }

    @Override
    public Bitmap getThumb(boolean isList) {
        return null;
    }

    @Override
    public Bundle getSpecialInfo(String type) {
        Bundle bundle = new Bundle();

        switch (type) {
            case DataConstant.ALL_CHILDREN_COUNT:
                bundle.putInt(type, -1);
                break;

            case DataConstant.OPERATION_PERMISSION:
                String permission = DataConstant.buildOperationPermission(true, false);
                bundle.putString(type, permission);
                break;

            default:
                return super.getSpecialInfo(type);
        }

        return bundle;
    }

    @Override
    public boolean isFolder() {
        return mHeader != null && mHeader.isDirectory();
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> fileList = new ArrayList<>();

        if (mArchive != null) {
            List<FileHeader> headers = mArchive.getFileHeaders();
            String archivePath = mFile.getPath();
            String name;

            for (FileHeader header : headers) {
                name = header.isUnicode() ? header.getFileNameW() : header.getFileNameString();

                if (inArchiveDirForRar(mPath, name, header.isDirectory())) {
                    fileList.add(new RarObject(header, archivePath));
                }
            }
        }

        return fileList;
    }

    private static boolean inArchiveDirForRar(String parentDir, String fileName, boolean isDir) {
        String suffix = ArchiveConstant.RAR_SUFFIX;

        if (parentDir.endsWith(suffix)) {
            if (!fileName.contains("/") && !fileName.contains("\\")) {
                return true;
            } else {
                return fileName.substring(fileName.length() - 1).equals("/") &&
                        !fileName.substring(0, fileName.lastIndexOf("/")).contains("/");
            }
        }

        parentDir = parentDir.substring(parentDir.lastIndexOf(suffix) + suffix.length() + 1);
        String tmpFilePath;
        if (fileName.equals(parentDir)) {
            return false;
        }

        if (fileName.contains("\\")) {
            fileName = fileName.replace("\\", "/");
        }
        if (isDir) {
            if (fileName.contains("/")) {
                tmpFilePath = fileName.substring(0, fileName.lastIndexOf("/"));
                if (tmpFilePath.equals(parentDir)) {
                    return true;
                }
            } else {
                if (fileName.equals(parentDir)) {
                    return true;
                }
            }
        } else {
            if (fileName.contains("/")) {
                tmpFilePath = fileName.substring(0, fileName.lastIndexOf("/"));
                if (parentDir.equals(tmpFilePath)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected String getArchiveFilePath() {
        return mFile != null ? mFile.getAbsolutePath() : null;
    }

    @Override
    public boolean rename(String newPath) {
        boolean result = super.rename(newPath);
        if (result) {
            mFile = new File(newPath);
            rarArray.remove(mPath);
            mPath = newPath;
        }

        return result;
    }

    @Override
    public boolean delete() {
        boolean result = false;

        if (mFile != null) {
            String path = mFile.getAbsolutePath();
            result = LocalFileHelper.deleteCompressObject(path);
            if (result) {
                ArchiveFactory.removeFileCache(path);
                rarArray.remove(path);
            }
        }

        return result;
    }

    @Override
    public boolean exists() {
        return mFile.exists();
    }

    @Override
    public int compressZipFiles(List<String> pathList, String zipFilePath, String srcFolderPath,
                                String password, Bundle args, CompressListener listener) {
        return ResultConstant.FAILED;
    }

    @Override
    public int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                                  final ExtractListener listener) throws InterruptedException {
        System.gc();

        final AtomicBoolean isCancel = new AtomicBoolean(false);
        UnrarCallback callback = new UnrarCallback() {

            long lastCompleted = 0;

            @Override
            public boolean isNextVolumeReady(File nextVolume) {
                return false;
            }

            @Override
            public void volumeProgressChanged(long current, long total) {
                if (listener != null) {
                    try {
                        listener.onProgress(current - lastCompleted);
                        lastCompleted = current;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isCancel.set(true);
                    }
                }
            }
        };

        try {
            Archive archive = new Archive(mFile, callback, password, false);
            FileHeader fileHeader = archive.nextFileHeader();
            String realDesFolder = getRealDesFolderPath(args, desFolder);

            File file;
            String fileName;
            String currentFilePath;

            while (fileHeader != null) {
                if (isCancel.get()) {
                    throw new InterruptedException();
                }

                fileName = getFileName(fileHeader);
                fileName = ArchiveNameUtils.createNoExistingName(fileName, realDesFolder);
                currentFilePath = FileUtils.concatPath(desFolder).concat(fileName);
                file = new File(currentFilePath);

                if (listener != null) {
                    listener.startExtractCallback(fileName);
                }

                if (!fileHeader.isDirectory()) {
                    ArchiveConstant.createParentDir(desFolder, currentFilePath);

                    FileOutputStream outputStream = new FileOutputStream(file);
                    archive.extractFile(fileHeader, outputStream, file);
                    outputStream.close();
                } else {
                    new File(currentFilePath).mkdirs();
                }

                if (listener != null) {
                    listener.finishExtractCallback(true, currentFilePath, fileName);
                }

                fileHeader = archive.nextFileHeader();
            }

            archive.close();
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            return ResultConstant.FAILED;
        } catch (RarException e) {
            e.printStackTrace();
            if (e.getType().equals(RarException.RarExceptionType.crcError)) {
                return ResultConstant.PASSWORD_ERROR;
            } else {
                return ResultConstant.FAILED;
            }
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                                 String charset, Bundle args, final ExtractListener listener)
            throws InterruptedException {
        if (passwordError(mFile, password)) {
            System.gc();
            return ResultConstant.PASSWORD_ERROR;
        } else {
            System.gc();
        }

        final AtomicBoolean isCancel = new AtomicBoolean(false);
        UnrarCallback callback = new UnrarCallback() {

            long lastCompleted = 0;

            @Override
            public boolean isNextVolumeReady(File nextVolume) {
                return false;
            }

            @Override
            public void volumeProgressChanged(long current, long total) {
                if (listener != null) {
                    try {
                        listener.onProgress(current - lastCompleted);
                        lastCompleted = current;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isCancel.set(true);
                    }
                }
            }
        };

        int selectedCount = selectedList.size();
        try {
            String realDesFolder = getRealDesFolderPath(args, desFolder);

            ArrayList<String> selectedPathList = new ArrayList<>();
            FileOutputStream outputStream;
            String currentFilePath;
            String fileName;
            File file;

            for (int i = 0; i < selectedCount; i++) {
                selectedPathList.add(ArchiveConstant.getCompressionFileInsidePath(selectedList.get(i).getPath()));
            }

            selectedCount = selectedPathList.size();
            Archive archive = new Archive(mFile, callback, password, false);
            FileHeader fileHeader = archive.nextFileHeader();

            while (fileHeader != null) {
                if (isCancel.get()) {
                    throw new InterruptedException();
                }

                fileName = getFileName(fileHeader);
                for (int index = 0; index < selectedCount; index++) {
                    if (fileName.startsWith(selectedPathList.get(index))) {
                        fileName = ArchiveNameUtils.createNoExistingName(fileName, realDesFolder);
                        currentFilePath = FileUtils.concatPath(desFolder).concat(fileName);
                        file = new File(currentFilePath);

                        if (listener != null) {
                            listener.startExtractCallback(fileName);
                        }

                        if (!fileHeader.isDirectory()) {
                            ArchiveConstant.createParentDir(desFolder, currentFilePath);

                            outputStream = new FileOutputStream(file);
                            archive.extractFile(fileHeader, outputStream, file);
                            outputStream.close();
                        } else {
                            new File(currentFilePath).mkdirs();
                            ArchiveConstant.addSuccessRecord(currentFilePath);
                        }

                        if (listener != null) {
                            listener.finishExtractCallback(true, currentFilePath, fileName);
                        }
                    }
                }

                fileHeader = archive.nextFileHeader();
            }

            archive.close();
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            return ResultConstant.FAILED;
        } catch (RarException e) {
            e.printStackTrace();
            if (e.getType().equals(RarException.RarExceptionType.crcError)) {
                return ResultConstant.PASSWORD_ERROR;
            } else {
                return ResultConstant.FAILED;
            }
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public String extractFile(String desFolder, String password, String charset, Bundle args) {
        System.gc();

        String desFilePath;
        try {
            Archive archive = new Archive(mFile, password, false);

            desFilePath = desFolder + File.separator + getFileName();
            File file = new File(desFilePath);
            FileOutputStream outputStream = new FileOutputStream(file);

            FileHeader fileHeader = null;
            String curFileName = getFileName();
            String fileName;
            List<FileHeader> rarFileHeaders = archive.getFileHeaders();
            for (int index = 0; index < rarFileHeaders.size(); index++) {
                fileHeader = rarFileHeaders.get(index);
                fileName = getFileName(fileHeader);
                fileName = FileUtils.getFileName(fileName);

                if (curFileName.equals(fileName)) {
                    break;
                }
            }

            archive.extractFile(fileHeader, outputStream, file);
            closeForRar(archive, outputStream);
        } catch (RarException | IOException | OutOfMemoryError e) {
            e.printStackTrace();
            desFilePath = null;
        }

        return desFilePath;
    }

    @Override
    public boolean isEncrypted() {
        return isEncrypted(mFile);
    }

    @Override
    public boolean isHeader() {
        return mHeader != null;
    }

    public static boolean isEncrypted(File file) {
        System.gc();

        Archive archive = null;
        FileOutputStream outputStream = null;
        String fileName;

        try {
            archive = new Archive(file, ArchiveConstant.NO_PASSWORD, false);
            FileHeader fileHeader = archive.nextFileHeader();

            fileName = getFileName(fileHeader);
            fileName = FileUtils.getFileName(fileName);
//            createParentDir(parentDirPath, testFilePath);
            File testFile = TmpFolderUtils.getTempFile(TimeUtils.getCurrentTimeString(), fileName);
            outputStream = new FileOutputStream(testFile);

            archive.extractFile(fileHeader, outputStream, testFile);
        } catch (RarException e) {
            e.printStackTrace();
            if (e.getType().equals(RarException.RarExceptionType.crcError)) {
                closeForRar(archive, outputStream);
                return true;
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    private static boolean passwordError(File file, String password) {
        Archive archive = null;
        FileOutputStream outputStream = null;
        String fileName;

        try {
            archive = new Archive(file, password, false);
            FileHeader fileHeader = archive.nextFileHeader();

            fileName = getFileName(fileHeader);
            fileName = FileUtils.getFileName(fileName);
            File testFile = TmpFolderUtils.getTempFile(TimeUtils.getCurrentTimeString(), fileName);
            outputStream = new FileOutputStream(testFile);

            archive.extractFile(fileHeader, outputStream, testFile);
        } catch (RarException e) {
            e.printStackTrace();
            if (e.getType().equals(RarException.RarExceptionType.crcError)) {
                closeForRar(archive, outputStream);
                return true;
            }
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    private static String getFileName(FileHeader fileHeader) {
        String fileNameString;
        if (fileHeader.isUnicode()) {
            fileNameString = fileHeader.getFileNameW();
        } else {
            fileNameString = fileHeader.getFileNameString();
        }

        if (fileNameString.contains("\\")) {
            fileNameString = fileNameString.replace("\\", "/");
        }

        return fileNameString;
    }

    private static void closeForRar(Archive archive, OutputStream outputStream) {
        try {
            if (archive != null) {
                archive.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
