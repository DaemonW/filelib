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
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.utils.IOUtils;
import com.grt.filemanager.util.ArchiveNameUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LocalFileHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by liwei on 2016/6/3.
 */
public class TarObject extends ArchiveObject {

    private static final ArrayMap<String, EntryNode> entryArray = new ArrayMap<>();

    private TarArchiveEntry mTarEntry;
    private File mFile;
    private EntryNode mNode;
    private boolean mIsTarFile;

    private String mPath;

    public TarObject(String path, String password) {
        this.mPath = path;
        this.mIsTarFile = ArchiveConstant.isArchiveFile(path);

        String archiveFilePath = ArchiveConstant.getArchiveFilePath(path);
        mFile = new File(archiveFilePath);
        mTarEntry = new TarArchiveEntry(mFile);
        initNode(mPath, mIsTarFile, mTarEntry);
    }

    public TarObject(TarArchiveEntry entry, String archiveFilePath, File file, EntryNode node) {
        this.mFile = file;
        this.mIsTarFile = false;
        this.mNode = node;

        if (entry == null) {
            this.mTarEntry = null;
            this.mPath = getRightPath(mNode.getPath());
        } else {
            this.mTarEntry = entry;
            this.mPath = archiveFilePath.concat("/").concat(getEntryName(entry));
        }
    }

    private void initNode(String path, boolean isTarFile, TarArchiveEntry entry) {
        if (isTarFile) {
            mNode = new EntryNode(path, false, null);
        } else {
            mNode = new EntryNode(path, entry.isDirectory(), entry);
        }
    }

    @Override
    public String getName() {
        String name;
        if (mTarEntry == null) {
            if (mNode != null) {
                name = mNode.getName();
            } else {
                name = mFile.getName();
            }
        } else {
            name = getName(mTarEntry.getName());
        }

        return name;
    }

    private static String getName(String entryName) {
        if (entryName.contains("/")) {
            if (entryName.endsWith("/")) {
                entryName = entryName.substring(0, entryName.lastIndexOf("/"));
            }
            return FileUtils.getFileName(entryName);
        } else {
            return entryName;
        }
    }

    private static String getEntryName(TarArchiveEntry entry) {
        String entryName = entry.getName();
        if (entryName.endsWith("/")) {
            entryName = entryName.substring(0, entryName.lastIndexOf("/"));
        }

        if (entryName.startsWith("./")) {
            entryName = entryName.substring(2, entryName.length());
        }

        return entryName;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public long getLastModified() {
        return (mTarEntry == null) ? mFile.lastModified() : mTarEntry.getLastModifiedDate().getTime();
    }

    @Override
    public long getSize() {
        if (mTarEntry != null) {
            if (mTarEntry.isDirectory()) {
                return -1;
            } else {
                return mTarEntry.getSize();
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
        if (mNode != null) {
            if (mNode.isFolder()) {
                return DataConstant.MIME_TYPE_FOLDER;
            } else {
                return FileUtils.getMiMeType(mNode.getName());
            }
        }

        return FileUtils.getMiMeType(mTarEntry.getName());
    }

    @Override
    public Bitmap getThumb(boolean isList) {
        return null;
    }

    @Override
    public boolean isFolder() {
        if (mTarEntry != null) {
            return mTarEntry.isDirectory();
        }

        return mNode != null && mNode.isFolder();
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> fileList = new ArrayList<>();
        String name;
        String archivePath = mFile.getPath();

        mNode = entryArray.get(mPath);
        if (mNode == null) {
            EntryNode treeNode = new EntryNode(archivePath, false, null);

            TarArchiveInputStream inputStream = null;
            try {
                inputStream = new TarArchiveInputStream(new FileInputStream(mFile));

                TarArchiveEntry entry;
                while (true) {
                    entry = inputStream.getNextTarEntry();
                    if (entry == null) {
                        break;
                    }

                    name = getEntryName(entry);
                    addNode(entryArray, name, entry, archivePath, treeNode);

                    entryArray.put(archivePath, treeNode);
                    mNode = entryArray.get(mPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }

        Map<String, EntryNode> children = mNode.getChildren();
        EntryNode node;
        for (Map.Entry<String, EntryNode> child : children.entrySet()) {
            node = child.getValue();
            fileList.add(new TarObject((TarArchiveEntry) node.getHeader(), archivePath, mFile, node));
        }

        return fileList;
    }

    @Override
    public boolean rename(String newPath) {
        boolean result = super.rename(newPath);
        if (result) {
            mFile = new File(newPath);
            mPath = newPath;
            mNode.setPath(newPath);
        }

        return result;
    }

    @Override
    public boolean delete() {
        if (mIsTarFile) {

            String path = mFile.getAbsolutePath();
            boolean result = LocalFileHelper.deleteCompressObject(path);
            if (result) {
                ArchiveFactory.removeFileCache(path);
            }

            return result;
        }

        return false;
    }

    @Override
    public boolean exists() {
        return !mIsTarFile || mFile.exists();
    }

    @Override
    protected String getArchiveFilePath() {
        return mFile.getAbsolutePath();
    }

    @Override
    public int compressZipFiles(List<String> pathList, String zipFilePath, String desFolderPath,
                                String password, Bundle args, final CompressListener listener)
            throws InterruptedException {
        int result = ResultConstant.SUCCESS;
        boolean useBasePath = args.getBoolean(DataConstant.USE_BASE_PATH, true);
        try {
            File file = new File(zipFilePath);
            TarArchiveOutputStream out = new TarArchiveOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file), BUFFER_LEN));

            int count = pathList.size();
            String name;
            for (int index = 0; index < count; index++) {

                file = new File(pathList.get(index));

                if (file.isFile()) {
                    if (useBasePath) {
                        name = file.getPath().replace(FileUtils.concatPath(desFolderPath), "");
                    } else {
                        name = file.getName();
                    }
                    writeFileEntryToTarFile(out, file, name, listener);
                } else {
                    ConcurrentLinkedQueue<File> qualifiedFileQueue = new ConcurrentLinkedQueue<>();
                    List<File> fileLists = new ArrayList<>();

                    FileUtils.addFiles(file, qualifiedFileQueue, fileLists);
                    writeFolderEntryToTarFile(out, fileLists, desFolderPath, listener);
                }
            }

            out.close();
        } catch (IOException | OutOfMemoryError e) {
            e.printStackTrace();
            result = ResultConstant.FAILED;
        }

        return result;
    }

    private void writeFolderEntryToTarFile(TarArchiveOutputStream out, List<File> fileLists,
                                           String baseDirPath, CompressListener listener)
            throws IOException, InterruptedException {
        if (baseDirPath == null) {
            throw new IOException();
        }

        File file;
        String name;
        for (int index = fileLists.size() - 1; 0 <= index; index--) {
            file = fileLists.get(index);
            name = file.getPath().replace(FileUtils.concatPath(baseDirPath), "");
            if (file.isDirectory()) {
                name = name.concat("/");
            }

            writeFileEntryToTarFile(out, file, name, listener);
        }
    }

    private void writeFileEntryToTarFile(TarArchiveOutputStream out, File srcFile, String name,
                                         CompressListener listener)
            throws IOException, InterruptedException {
        String path = srcFile.getPath();
        if (listener != null) {
            listener.startCompressCallback(path);
        }

        TarArchiveEntry entry = new TarArchiveEntry(name);
        boolean isFile = srcFile.isFile();
        entry.setSize(isFile ? srcFile.length() : 0);
        out.putArchiveEntry(entry);

        if (isFile) {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(srcFile), BUFFER_LEN);
                copyStream(is, out, listener);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        out.closeArchiveEntry();

        if (listener != null) {
            listener.finishCompressCallback(path);
        }
    }

    private static void copyStream(InputStream inputStream, OutputStream outputStream,
                                   CompressListener listener) throws IOException, InterruptedException {
        byte[] buffer = new byte[BUFFER_LEN];

        int count;
        do {

            count = inputStream.read(buffer, 0, BUFFER_LEN);
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
    }

    @Override
    public int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                                  ExtractListener listener) throws InterruptedException {
        int result = ResultConstant.SUCCESS;
        TarArchiveInputStream inputStream = null;
        try {
            inputStream = new TarArchiveInputStream(new FileInputStream(mFile));

            TarArchiveEntry entry;
            String name;
            while (true) {
                entry = inputStream.getNextTarEntry();
                if (entry == null) {
                    break;
                }

                name = getEntryName(entry);
                extractTarFileImplement(desFolder, inputStream, entry, name, args, listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = ResultConstant.FAILED;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    @Override
    public int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                                 String charset, Bundle args, ExtractListener listener)
            throws InterruptedException {
        int result = ResultConstant.SUCCESS;
        int count = selectedList.size();
        ArrayList<String> selectedPathList = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            selectedPathList.add(ArchiveConstant.getCompressionFileInsidePath(selectedList.get(index).getPath()));
        }

        TarArchiveInputStream inputStream = null;
        try {
            inputStream = new TarArchiveInputStream(new FileInputStream(mFile));

            count = selectedPathList.size();
            TarArchiveEntry entry;
            String name;
            while (true) {
                entry = inputStream.getNextTarEntry();
                if (entry == null) {
                    break;
                }

                name = getEntryName(entry);
                for (int index = 0; index < count; index++) {
                    if (name.startsWith(selectedPathList.get(index))) {
                        result = extractTarFileImplement(desFolder, inputStream, entry, name, args, listener);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
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
            desFilePath = FileUtils.concatPath(desFolder).concat(mTarEntry.getName());
        }

        return desFilePath;
    }

    private static int extractTarFileImplement(String desDirPath, TarArchiveInputStream inputStream,
                                               TarArchiveEntry entry, String name, Bundle args,
                                               ExtractListener listener)
            throws IOException, InterruptedException {

        if (listener != null) {
            listener.startExtractCallback(name);
        }

        String realDesFolder = getRealDesFolderPath(args, desDirPath);
        name = ArchiveNameUtils.createNoExistingName(name, realDesFolder);//如有重名，创建新名
        String currentFilePath = FileUtils.concatPath(desDirPath).concat(name);
        ArchiveConstant.createParentDir(desDirPath, currentFilePath);
        File target = new File(currentFilePath);

        if (entry.isDirectory()) {
            target.mkdirs();
        } else if (entry.getSize() > 0) {
            long left = entry.getSize();
            byte[] temp = new byte[BUFFER_LEN];
            FileOutputStream out = new FileOutputStream(target);
            int read;

            do {
                read = inputStream.read(temp, 0, left > BUFFER_LEN ? BUFFER_LEN : (int) left);
                out.write(temp, 0, read);
                left -= read;

                if (listener != null) {
                    listener.onProgress(read);
                }
            } while (left > 0);

            out.flush();
            out.close();
        }

        if (listener != null) {
            listener.finishExtractCallback(true, currentFilePath, name);
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public boolean isHeader() {
        return !mIsTarFile;
    }

}
