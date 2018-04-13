package com.grt.filemanager.model.imp.local.archive;

import com.grt.filemanager.R;
import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.model.ArchiveObject;
import com.grt.filemanager.model.CompressListener;
import com.grt.filemanager.model.ExtractListener;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.util.ArchiveNameUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LocalFileHelper;
import com.grt.filemanager.util.PreferenceUtils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.SparseIntArray;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.UnzipParameters;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liwei on 2016/3/4.
 * zip压缩文件实现类
 */
public class ZipObject extends ArchiveObject {

    private static final ArrayMap<String, ZipFile> zipArray = new ArrayMap<>();
    private static final ArrayMap<String, EntryNode> entryArray = new ArrayMap<>();

    private static final SparseIntArray zipCompressLevel = new SparseIntArray() {
        {
            put(ArchiveConstant.COMPRESS_LEVEL_FASTEST, Zip4jConstants.DEFLATE_LEVEL_FASTEST);
            put(ArchiveConstant.COMPRESS_LEVEL_FAST, Zip4jConstants.DEFLATE_LEVEL_FAST);
            put(ArchiveConstant.COMPRESS_LEVEL_NORMAL, Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            put(ArchiveConstant.COMPRESS_LEVEL_MAX, Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
            put(ArchiveConstant.COMPRESS_LEVEL_ULTRA, Zip4jConstants.DEFLATE_LEVEL_ULTRA);
        }
    };

    protected ZipFile mZip;
    protected File mFile;
    private EntryNode mNode;
    protected FileHeader mHeader;
    private String mPath;

    public ZipObject(String path, String password) {
        mPath = path;

        String archiveFilePath;
        boolean isZipFile = ArchiveConstant.isArchiveFile(path);
        if (!isZipFile) {
            archiveFilePath = ArchiveConstant.getArchiveFilePath(path);
        } else {
            archiveFilePath = path;
            this.mHeader = null;
        }
        mFile = new File(archiveFilePath);
        mZip = zipArray.get(archiveFilePath);
        if (mZip == null) {
            try {
                mZip = new ZipFile(mFile);

                zipArray.put(archiveFilePath, mZip);
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        setCharset(mZip);

        if (!isZipFile) {
            mHeader = getFileHeader(path);
            mNode = new EntryNode(path, mHeader.isDirectory(), mHeader);
        } else {
            mNode = new EntryNode(path, false, null);
        }
    }

    private FileHeader getFileHeader(String filePath) {
        FileHeader fileHeader = null;

        List<FileHeader> headers;
        try {
            headers = mZip.getFileHeaders();

            int count = headers.size();
            for (int index = 0; index < count; index++) {
                fileHeader = headers.get(index);
                String fileName = fileHeader.getFileName();

                if (filePath.contains(fileName)) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileHeader;
    }

    public ZipObject(FileHeader header, EntryNode node, String archiveFilePath) {
        this.mZip = zipArray.get(archiveFilePath);
        setCharset(mZip);
        this.mFile = mZip.getFile();
        this.mNode = node;

        if (header == null) {
            this.mHeader = null;
            this.mPath = getRightPath(mNode.getPath());
        } else {
            this.mHeader = header;
            this.mPath = archiveFilePath.concat("/").concat(getInsidePath(header));
        }
    }

    private static void setCharset(ZipFile zipFile) {
        String charset = context.getResources().getStringArray(R.array.encodings)
                [PreferenceUtils.getPrefInt(SettingConstant.ARCHIVE_ENCODE, SettingConstant.ARCHIVE_ENCODE_DEFAULT)];
        try {
            zipFile.setFileNameCharset(charset);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    protected static String getInsidePath(FileHeader header) {
        String name = header.getFileName();
        if (name.contains("\\")) {
            name = name.replace("\\", "/");
        }

        if (name.endsWith("/")) {
            name = name.substring(0, name.lastIndexOf("/"));
        }

        return name;
    }

    @Override
    public String getName() {
        String name;
        if (mHeader == null) {
            if (mNode != null) {
                name = mNode.getName();
            } else {
                name = mFile.getName();
            }
        } else {
            name = ArchiveConstant.parserZipFileName(mHeader.getFileName());
        }

        return name;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public long getLastModified() {
        return mHeader == null ? mFile.lastModified() : Zip4jUtil.dosToJavaTme(mHeader.getLastModFileTime());
    }

    @Override
    public long getSize() {
        if (mHeader != null) {
            if (mHeader.isDirectory()) {
                return -1;
            } else {
                return mHeader.getCompressedSize();
            }
        }

        if (mNode != null && mNode.isFolder()) {
            return -1;
        }

        if (mFile != null) {
            return mFile.length();
        }

        return -1;
    }

    @Override
    public String getMimeType() {
        if (mHeader != null) {
            return FileUtils.getMiMeType(mHeader.getFileName());
        }

        if (mNode != null) {
            if (mNode.isFolder()) {
                return DataConstant.MIME_TYPE_FOLDER;
            } else {
                return FileUtils.getMiMeType(mNode.getName());
            }
        }

        return DataConstant.MIME_TYPE_FOLDER;
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
        if (mHeader != null) {
            return mHeader.isDirectory();
        }

        return mNode != null && mNode.isFolder();
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> fileList = new ArrayList<>();

        try {
            String archiveFilePath = ArchiveConstant.getArchiveFilePath(mPath);
            if (SettingConstant.CHANGE_ARCHIVE_ENCODE) {
                zipArray.remove(archiveFilePath);
                entryArray.clear();
                mZip = new ZipFile(archiveFilePath);
                setCharset(mZip);
                zipArray.put(archiveFilePath, mZip);
            }

            mNode = entryArray.get(mPath);
            if (SettingConstant.CHANGE_ARCHIVE_ENCODE || mNode == null) {

                EntryNode treeNode = new EntryNode(archiveFilePath, false, null);

                List<FileHeader> headers = mZip.getFileHeaders();
                String archiveName;
                for (FileHeader header : headers) {
                    archiveName = header.getFileName();
                    addNode(entryArray, archiveName, header, archiveFilePath, treeNode);
                }

                entryArray.put(archiveFilePath, treeNode);
                mNode = entryArray.get(mPath);
            }

            Map<String, EntryNode> children = mNode.getChildren();
            EntryNode node;
            for (Map.Entry<String, EntryNode> child : children.entrySet()) {
                node = child.getValue();
                fileList.add(new ZipObject((FileHeader) node.getHeader(), node, archiveFilePath));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }

    @Override
    public boolean rename(String newPath) {
        String oldPath = mPath;
        boolean result = super.rename(newPath);
        if (result) {
            mFile = new File(newPath);
            mPath = newPath;
            mNode.setPath(newPath);
            try {
                mZip = new ZipFile(mFile);
            } catch (ZipException e) {
                e.printStackTrace();
            }
            zipArray.remove(oldPath);
            entryArray.clear();
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
                zipArray.remove(path);
                entryArray.clear();
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
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        parameters.setCompressionLevel(zipCompressLevel.get(args.getInt(ArchiveConstant.COMPRESS_LEVEL,
                ArchiveConstant.COMPRESS_LEVEL_NORMAL)));

        parameters.setPassword(password);
        if (!TextUtils.isEmpty(password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
        }

        int result = ResultConstant.SUCCESS;
        try {
            mZip.setRunInThread(true);
            ProgressMonitor progressMonitor = mZip.getProgressMonitor();
            boolean isEncryptFiles = parameters.isEncryptFiles();
            long lastCompleted;
            long completed;
            long count;
            File file;

            for (String path : pathList) {
                if (listener != null) {
                    listener.startCompressCallback(path);
                }

                completed = 0;
                file = new File(path);
                if (file.isDirectory()) {
                    mZip.addFolder(file, parameters);
                } else {
                    mZip.addFile(file, parameters);
                }

                while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
                    if (listener != null) {
                        lastCompleted = completed;
                        completed = progressMonitor.getWorkCompleted();
                        count = completed - lastCompleted;
                        if (isEncryptFiles) {
                            count /= 2;
                        }
                        listener.onProgress(count);
                    }
                    Thread.sleep(500);
                }

                if (listener != null) {
                    listener.finishCompressCallback(file.getPath());
                }
            }
        } catch (ZipException e) {
            e.printStackTrace();
            return ResultConstant.FAILED;
        }

        ArchiveFactory.removeFileCache(zipFilePath);
        zipArray.remove(zipFilePath);
        entryArray.clear();

        return result;
    }

    @Override
    public int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                                  ExtractListener listener) {
        try {
            //不能修改顺序
            ZipFile zipFile = new ZipFile(mFile);

            if (setupArchiveArgs(zipFile, password, charset) == ResultConstant.PASSWORD_ERROR) {
                return ResultConstant.PASSWORD_ERROR;
            }

            UnzipParameters parameters = new UnzipParameters();
            List fileHeaders = zipFile.getFileHeaders();
            int count = fileHeaders.size();
            FileHeader fileHeader;
            String name;
            int result;

            for (int index = 0; index < count; index++) {
                fileHeader = (FileHeader) fileHeaders.get(index);
                name = getInsidePath(fileHeader);
                result = extractFileImpl(zipFile, fileHeader, name, desFolder, parameters, args, listener);
                if (result != ResultConstant.SUCCESS) {
                    return result;
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultConstant.CANCEL;
        } catch (ZipException e) {
            e.printStackTrace();
            return getFailedResult(e);
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                                 String charset, Bundle args, ExtractListener listener) {
        try {
            //不能修改顺序
            ZipFile zipFile = new ZipFile(mFile);

            if (setupArchiveArgs(zipFile, password, charset) == ResultConstant.PASSWORD_ERROR) {
                return ResultConstant.PASSWORD_ERROR;
            }

            UnzipParameters parameters = new UnzipParameters();
            List fileHeaders = zipFile.getFileHeaders();
            int selectedCount = selectedList.size();
            int headerCount = fileHeaders.size();
            String desExtractFilePath;
            FileHeader fileHeader;
            String fileName;
            int result;

//            String previewEncoding = context.getResources().getStringArray(R.array.encodings)[
//                    PreferenceUtils.getPrefInt(SettingConstant.ARCHIVE_ENCODE, SettingConstant.ARCHIVE_ENCODE_DEFAULT)];

            for (int i = 0; i < selectedCount; i++) {

                desExtractFilePath = ArchiveConstant.getCompressionFileInsidePath(selectedList.get(i).getPath());
//                desExtractFilePath = new String(desExtractFilePath.getBytes(previewEncoding), charset);

                for (int index = 0; index < headerCount; index++) {
                    fileHeader = (FileHeader) fileHeaders.get(index);
                    fileName = getInsidePath(fileHeader);

                    if (fileName.startsWith(desExtractFilePath)) {
                        result = extractFileImpl(zipFile, fileHeader, fileName, desFolder,
                                parameters, args, listener);
                        if (result != ResultConstant.SUCCESS) {
                            return result;
                        }
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultConstant.CANCEL;
        } catch (ZipException e) {
            e.printStackTrace();
            return getFailedResult(e);
        }

        return ResultConstant.SUCCESS;
    }

    //不能修改顺序
    protected int setupArchiveArgs(ZipFile zipFile, String password, String charset) throws ZipException {
        if (!TextUtils.isEmpty(charset)) {
            zipFile.setFileNameCharset(charset);
        }

        if (zipFile.isEncrypted()) {
            if (password != null && !password.isEmpty()) {
                zipFile.setPassword(password);
            } else {
                return ResultConstant.PASSWORD_ERROR;
            }
        }

        return ResultConstant.SUCCESS;
    }

    protected int extractFileImpl(ZipFile zipFile, FileHeader fileHeader, String name, String desFolder,
                                  UnzipParameters parameters, Bundle args, ExtractListener listener)
            throws ZipException, InterruptedException {
        if (listener != null) {
            listener.startExtractCallback(name);
        }

        String realDesFolder = getRealDesFolderPath(args, desFolder);
        name = ArchiveNameUtils.createNoExistingName(name, realDesFolder);//如有重名，创建新名
        String currentFilePath = FileUtils.concatPath(desFolder) + name;
        ArchiveConstant.createParentDir(desFolder, currentFilePath);
        if (fileHeader.isDirectory()) {
            new File(currentFilePath).mkdirs();
        } else {
            zipFile.extractFile(fileHeader, desFolder, parameters, name);
        }

        if (listener != null) {
            listener.onProgress(fileHeader.getCompressedSize());

            listener.finishExtractCallback(true, currentFilePath, name);
        }

        return ResultConstant.SUCCESS;
    }

    private int getFailedResult(ZipException e) {
        int result = ResultConstant.FAILED;

        String message = null;
        if (e != null) {
            message = e.getMessage();
        }

        if (message != null) {
            if (message.endsWith("Wrong Password?")) {
                result = ResultConstant.PASSWORD_ERROR;
            }
        }

        return result;
    }

    @Override
    public String extractFile(String desFolder, String password, String charset, Bundle args) {
        mZip.setRunInThread(false);
        String desFilePath = null;

        try {
            if (!TextUtils.isEmpty(password)) {
                mZip.setPassword(password);
            }

            if (!TextUtils.isEmpty(charset)) {
                mZip.setFileNameCharset(charset);
            }

            String fileName = getRightPath(mHeader.getFileName());

            mZip.extractFile(mHeader, desFolder, new UnzipParameters(), fileName);
            desFilePath = FileUtils.concatPath(desFolder).concat(fileName);
            mZip.setPassword("FileUtils.concatPath(");//设置错误密码
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return desFilePath;
    }

    @Override
    public boolean isEncrypted() {
        if (mZip != null) {
            try {
                return mZip.isEncrypted();
            } catch (ZipException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public boolean isHeader() {
        return !mPath.equals(mFile.getPath());
    }

    public static boolean isEncrypted(File file) {
        try {
            ZipFile zip = new ZipFile(file);
            return zip.isEncrypted();
        } catch (ZipException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected String getArchiveFilePath() {
        return mFile != null ? mFile.getAbsolutePath() : null;
    }

}
