package com.grt.filemanager.model.imp.local.archive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.ArchiveObject;
import com.grt.filemanager.model.CompressListener;
import com.grt.filemanager.model.ExtractListener;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.PasswordRequiredException;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.sevenz.SevenZFile;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import com.grt.filemanager.util.ArchiveNameUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LocalFileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by liwei on 2016/1/4.
 * 7z压缩文件实现类
 */
public class SevenZObject extends ArchiveObject {

    private static ArrayMap<String, SevenZFile> sevenZArray = new ArrayMap<>();
    private static final int bufferSize = 8192;

    private SevenZFile mSevenZ;
    private File mFile;
    private SevenZArchiveEntry mEntry;

    private String mPassword;
    private String mPath;

    public SevenZObject(String path, String password) {
        this.mPath = path;
        this.mPassword = password;

        String archiveFilePath;
        boolean isSevenZFile = ArchiveConstant.isArchiveFile(path);
        if (!isSevenZFile) {
            archiveFilePath = ArchiveConstant.getArchiveFilePath(path);
        } else {
            archiveFilePath = path;
            this.mEntry = null;
        }

        this.mSevenZ = sevenZArray.get(archiveFilePath);
        if (mSevenZ == null) {
            try {
                mFile = new File(archiveFilePath);
                mSevenZ = getSevenZFile(mFile, mPassword);
                sevenZArray.put(archiveFilePath, mSevenZ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mFile = new File(archiveFilePath);
        }

        if (!isSevenZFile) {
            this.mEntry = getFileHeader(path);
        }
    }

    private SevenZArchiveEntry getFileHeader(String filePath) {
        SevenZArchiveEntry entry = null;
        String name;

        do {
            try {
                entry = mSevenZ.getNextEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (entry == null) {
                break;
            }

            name = entry.getName();
            if (filePath.contains(name)) {
                break;
            }
        } while (true);

        return entry;
    }

    public SevenZObject(SevenZArchiveEntry entry, String archiveFilePath, File file) {
        this.mSevenZ = sevenZArray.get(file.getPath());
        this.mFile = file;
        this.mEntry = entry;
        this.mPath = archiveFilePath.concat("/").concat(entry.getName());
    }

    @Override
    public String getName() {
        String name;
        if (mEntry == null) {
            name = mFile.getName();
        } else {
            name = getName(mEntry);
        }

        return name;
    }

    private String getName(SevenZArchiveEntry entry) {
        String name = entry.getName();
        if (name.contains("/")) {
            if (name.endsWith("/")) {
                name = name.substring(name.lastIndexOf("/") + 1);
            }
            name = FileUtils.getFileName(name);
        }

        return name;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public long getLastModified() {
        return mEntry == null ? mFile.lastModified() : mEntry.getLastModifiedDate().getTime();
    }

    @Override
    public long getSize() {
        return mEntry == null ? mFile.length() : mEntry.getSize();
    }

    @Override
    public String getMimeType() {
        if (mEntry != null) {
            if (mEntry.isDirectory()) {
                return DataConstant.MIME_TYPE_FOLDER;
            } else {
                return FileUtils.getMiMeType(mEntry.getName());
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
        return mEntry != null && mEntry.isDirectory();
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> fileList = new ArrayList<>();
        String name;
        String archivePath = mFile.getPath();

        Iterable<SevenZArchiveEntry> entryIterable = mSevenZ.getEntries();
        for (SevenZArchiveEntry entry : entryIterable) {

            name = entry.getName();
            if (inArchiveDirForSevenZ(mPath, name, entry.isDirectory())) {
                fileList.add(new SevenZObject(entry, archivePath, mFile));
            }
        }

        return fileList;
    }

    private static boolean inArchiveDirForSevenZ(String parentDir, String fileName, boolean isDir) {
        String suffix = ArchiveConstant.SEVEN_Z_SUFFIX;

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
            sevenZArray.remove(mPath);
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
                sevenZArray.remove(path);
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
                                String password, Bundle args, CompressListener listener)
            throws InterruptedException {
        int result = ResultConstant.SUCCESS;
        boolean useBasePath = args.getBoolean(DataConstant.USE_BASE_PATH, true);

        try {
            File file;
            SevenZOutputFile sevenZOutputFile;
            sevenZOutputFile = new SevenZOutputFile(new File(zipFilePath));

            int count = pathList.size();
            for (int index = 0; index < count; index++) {

                file = new File(pathList.get(index));

                if (file.isFile()) {
                    writeContentOfEntry2SevenZ(sevenZOutputFile, file, useBasePath, srcFolderPath, listener);
                } else {
                    ConcurrentLinkedQueue<File> qualifiedFileQueue = new ConcurrentLinkedQueue<>();
                    List<File> fileLists = new ArrayList<>();

                    FileUtils.addFiles(file, qualifiedFileQueue, fileLists);
                    writeDir2SevenZ(sevenZOutputFile, fileLists, srcFolderPath, listener);
                }
            }

            sevenZOutputFile.close();
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            result = ResultConstant.FAILED;
        }

        return result;
    }

    private static void writeContentOfEntry2SevenZ(SevenZOutputFile sevenZOutputFile, File file,
                                                   boolean useBasePath, String baseDirPath,
                                                   CompressListener listener)
            throws IOException, InterruptedException {

        String path = file.getPath();
        String fileName;
        if (useBasePath) {
            fileName = path.replace(FileUtils.concatPath(baseDirPath), "");
        } else {
            fileName = file.getName();
        }
        SevenZArchiveEntry entry = sevenZOutputFile.createArchiveEntry(file, fileName);
        sevenZOutputFile.putArchiveEntry(entry);

        if (listener != null) {
            listener.startCompressCallback(path);
        }

        if (file.isFile()) {
            byte[] contentOfEntry = new byte[bufferSize];
            FileInputStream inputStream = new FileInputStream(file);
            int count;

            do {
                count = inputStream.read(contentOfEntry, 0, bufferSize);
                if (count != -1) {
                    sevenZOutputFile.write(contentOfEntry, 0, count);

                    if (listener != null) {
                        listener.onProgress(count);
                    }
                }
            } while (count != -1);
        }

        sevenZOutputFile.closeArchiveEntry();

        if (listener != null) {
            listener.finishCompressCallback(path);
        }
    }

    private static void writeDir2SevenZ(SevenZOutputFile sevenZOutputFile, List<File> fileLists,
                                        String baseDirPath, CompressListener listener)
            throws IOException, InterruptedException {
        if (baseDirPath == null) {
            throw new IOException();
        }

        for (int index = fileLists.size() - 1; 0 <= index; index--) {
            writeContentOfEntry2SevenZ(sevenZOutputFile, fileLists.get(index), true, baseDirPath, listener);
        }
    }

    @Override
    public int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                                  ExtractListener listener) throws InterruptedException {
        try {
            SevenZFile sevenZFile = getSevenZFile(mFile, password);

            do {
                SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                if (entry == null) {
                    break;
                }

                extractSevenZFileImplement(desFolder, sevenZFile, entry, args, listener);
            } while (true);

            sevenZFile.close();
        } catch (PasswordRequiredException exception) {
            return ResultConstant.PASSWORD_ERROR;
        } catch (Exception | OutOfMemoryError e) {
            return ResultConstant.FAILED;
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                                 String charset, Bundle args, ExtractListener listener) throws InterruptedException {
        int result = ResultConstant.SUCCESS;
        SevenZArchiveEntry entry;

        try {
            int count = selectedList.size();
            ArrayList<String> selectedPathList = new ArrayList<>();

            for (int index = 0; index < count; index++) {
                selectedPathList.add(ArchiveConstant.getCompressionFileInsidePath(selectedList.get(index).getPath()));
            }

            SevenZFile sevenZFile = getSevenZFile(mFile, password);
            count = selectedPathList.size();

            while ((entry = sevenZFile.getNextEntry()) != null) {
                for (int index = 0; index < count; index++) {
                    if (entry.getName().startsWith(selectedPathList.get(index))) {
                        extractSevenZFileImplement(desFolder, sevenZFile, entry, args, listener);
                    }
                }
            }

            sevenZFile.close();
        } catch (PasswordRequiredException exception) {
            result = ResultConstant.PASSWORD_ERROR;
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            result = ResultConstant.FAILED;
        }

        return result;
    }

    @Override
    public String extractFile(String desFolder, String password, String charset, Bundle args) {
        String desFilePath = null;

        int result = ResultConstant.SUCCESS;
        try {
            List<FileInfo> list = new ArrayList<>();
            list.add(this);
            result = extractInsideFile(list, desFolder, password, charset, args, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (result == ResultConstant.SUCCESS) {
            desFilePath = FileUtils.concatPath(desFolder).concat(mEntry.getName());
        }

        return desFilePath;
    }

    private int extractSevenZFileImplement(String desDirPath, SevenZFile sevenZFile,
                                           SevenZArchiveEntry entry, Bundle args, ExtractListener listener)
            throws IOException, InterruptedException {
        String fileName = entry.getName();
        if (listener != null) {
            listener.startExtractCallback(fileName);
        }

        String realDesFolder = getRealDesFolderPath(args, desDirPath);
        fileName = ArchiveNameUtils.createNoExistingName(fileName, realDesFolder);//如有重名，创建新名
        String currentFilePath = FileUtils.concatPath(desDirPath).concat(fileName);

        ArchiveConstant.createParentDir(desDirPath, currentFilePath);
        File target = new File(currentFilePath);
        if (entry.isDirectory()) {
            target.mkdirs();
        } else if (entry.getSize() > 0) {
            long left = entry.getSize();
            int bufferCount = 4096;
            byte[] temp = new byte[bufferCount];
            FileOutputStream out = new FileOutputStream(target);
            int read;

            do {
                read = sevenZFile.read(temp, 0, left > bufferCount ? bufferCount : (int) left);
                out.write(temp, 0, read);
                left -= read;

                if (listener != null) {
                    listener.onProgress(read);
                }
            } while (left > 0);

            out.flush();
            out.close();
        } else {
            FileOutputStream out = new FileOutputStream(target);
            out.flush();
            out.close();
        }

        if (entry.getHasLastModifiedDate()) {
            target.setLastModified(entry.getLastModifiedDate().getTime());
        }

        if (listener != null) {
            listener.finishExtractCallback(true, currentFilePath, fileName);
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public boolean isEncrypted() {
        return isEncrypted(mFile);
    }

    @Override
    public boolean isHeader() {
        return mEntry != null;
    }

    private static SevenZFile getSevenZFile(File file, String password) throws IOException {
        SevenZFile sevenZFile;

        if (TextUtils.isEmpty(password)) {
            sevenZFile = new SevenZFile(file);
        } else {
            byte[] passwordBytes = getLittleEndianPassword(password);
            sevenZFile = new SevenZFile(file, passwordBytes);
        }

        return sevenZFile;
    }

    public static boolean isEncrypted(File file) {
        SevenZFile sevenZFile;
        try {
            sevenZFile = new SevenZFile(file);
            do {
                SevenZArchiveEntry entry = sevenZFile.getNextEntry();
                if (entry == null) {
                    break;
                }
                if (!entry.isDirectory() && entry.getSize() > 0) {
                    sevenZFile.read();
                    break;
                }
            } while (true);

            sevenZFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        return false;
    }

    private static byte[] getLittleEndianPassword(String password) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(password.length() * 2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.asCharBuffer().put(password);
        return byteBuffer.array();
    }

}
