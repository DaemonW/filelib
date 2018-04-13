package com.grt.filemanager.model.imp.local.archive;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.model.ArchiveObject;
import com.grt.filemanager.model.CompressListener;
import com.grt.filemanager.model.ExtractListener;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.archivers.zip.ZipFile;
import com.grt.filemanager.model.imp.local.archive.inter.org.apache.commons.compress.utils.IOUtils;
import com.grt.filemanager.util.ArchiveNameUtils;
import com.grt.filemanager.util.FeThumbUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LocalFileHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ApkObject extends ArchiveObject {

    private static final ArrayMap<String, ZipFile> zipArray = new ArrayMap<>();
    private static final ArrayMap<String, EntryNode> entryArray = new ArrayMap<>();

    protected ZipFile mZip;
    protected File mFile;
    private EntryNode mNode;
    private ZipArchiveEntry mZipArchiveEntry;
    private String mPath;

    public ApkObject(String path, String password) {
        mPath = path;

        String archiveFilePath;
        boolean isZipFile = ArchiveConstant.isArchiveFile(path);
        if (!isZipFile) {
            archiveFilePath = ArchiveConstant.getArchiveFilePath(path);
        } else {
            archiveFilePath = path;
            this.mZipArchiveEntry = null;
        }
        mFile = new File(archiveFilePath);
        mZip = zipArray.get(archiveFilePath);

        initZip(archiveFilePath);

        setCharset(mZip);

        if (!isZipFile) {
            mZipArchiveEntry = new ZipArchiveEntry(mFile.getName());
            mNode = new EntryNode(path, mZipArchiveEntry.isDirectory(), mZipArchiveEntry);
        } else {
            mNode = new EntryNode(path, false, null);
        }
    }

    public ApkObject(ZipArchiveEntry header, EntryNode node, String archiveFilePath, File file) {
        this.mZip = zipArray.get(archiveFilePath);
        initZip(archiveFilePath);
        setCharset(mZip);
        this.mNode = node;
        this.mFile = file;
        if (header == null) {
            this.mZipArchiveEntry = null;
            this.mPath = getRightPath(mNode.getPath());
        } else {
            this.mZipArchiveEntry = header;
            this.mPath = archiveFilePath.concat("/").concat(getInsidePath(header));
        }
    }

    private static void setCharset(ZipFile zipFile) {
//        String charset = context.getResources().getStringArray(R.array.encodings)
//                [PreferenceUtils.getPrefInt(SettingConstant.ARCHIVE_ENCODE, SettingConstant.ARCHIVE_ENCODE_DEFAULT)];
    }

    protected static String getInsidePath(ZipArchiveEntry header) {
        String name = header.getName();
        if (name.contains("\\")) {
            name = name.replace("\\", "/");
        }

        if (name.endsWith("/")) {
            name = name.substring(0, name.lastIndexOf("/"));
        }

        return name;
    }

    private static String getName2(String entryName) {
        if (entryName.contains("/")) {
            if (entryName.endsWith("/")) {
                entryName = entryName.substring(0, entryName.lastIndexOf("/"));
            }
            return FileUtils.getFileName(entryName);
        } else {
            return entryName;
        }
    }

    private void initZip(String archiveFilePath) {
        if (mZip == null) {
            try {
                mZip = new ZipFile(mFile);

                zipArray.put(archiveFilePath, mZip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        String name;
        if (mZipArchiveEntry == null) {
            if (mNode != null) {
                name = mNode.getName();
            } else {
                name = mFile.getName();
            }
        } else {
            name = ArchiveConstant.parserZipFileName(mZipArchiveEntry.getName());
        }

        return name;
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public long getLastModified() {
        return (mZipArchiveEntry == null) ? mFile.lastModified() : mZipArchiveEntry.getTime();
    }

    @Override
    public long getSize() {
        if (mZipArchiveEntry != null) {
            if (mZipArchiveEntry.isDirectory()) {
                return -1;
            } else {
                return mZipArchiveEntry.getCompressedSize();
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
        return FileUtils.getMiMeType(getName2(mZipArchiveEntry.getName()));
    }

    @Override
    public Bitmap getThumb(boolean isList) {
        if (mZipArchiveEntry == null) {
            return FeThumbUtils.getThumbFromDb(context, FileUtils.getMiMeType(mFile.getName()),
                    mPath, isList);
        } else {
            return null;
        }
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
        if (mZipArchiveEntry != null) {
            return mZipArchiveEntry.isDirectory();
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

                Enumeration<ZipArchiveEntry> headers = mZip.getEntries();

                ZipArchiveEntry zipArchiveEntry;
                while (headers.hasMoreElements()) {
                    zipArchiveEntry = headers.nextElement();
                    addNode(entryArray, zipArchiveEntry.getName(), zipArchiveEntry, archiveFilePath, treeNode);
                }

                entryArray.put(archiveFilePath, treeNode);
                mNode = entryArray.get(mPath);
            }

            Map<String, EntryNode> children = mNode.getChildren();
            EntryNode node;
            for (Map.Entry<String, EntryNode> child : children.entrySet()) {
                node = child.getValue();
                fileList.add(new ApkObject((ZipArchiveEntry) node.getHeader(), node, archiveFilePath, mFile));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }

    protected static String getEntryName(ZipArchiveEntry entry) {
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
    public boolean rename(String newPath) {
        String oldPath = mPath;
        boolean result = super.rename(newPath);
        if (result) {
            mFile = new File(newPath);
            mPath = newPath;
            mNode.setPath(newPath);
            try {
                mZip = new ZipFile(mFile);
            } catch (Exception e) {
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
    public int compressZipFiles(List<String> pathList, String zipFilePath, String desFolderPath,
                                String password, Bundle args, CompressListener listener)
            throws InterruptedException {
        return ResultConstant.FAILED;
    }

    @Override
    public int extractArchiveFile(String desFolder, String password, String charset, Bundle args,
                                  ExtractListener listener) {
        int result = ResultConstant.SUCCESS;
        InputStream inputStream = null;

        if (mZip == null) {
            try {
                mZip = new ZipFile(mFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            inputStream = mZip.getInputStream(mZipArchiveEntry);
            Enumeration<ZipArchiveEntry> entryEnumeration = mZip.getEntries();
            ZipArchiveEntry entry;
            String name;
            while (entryEnumeration.hasMoreElements()) {
                entry = entryEnumeration.nextElement();
                if (entry == null) {
                    break;
                }
                name = getEntryName(entry);
                inputStream = mZip.getInputStream(entry);
                extractApkFileImplement(desFolder, inputStream, entry, name, args, listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = ResultConstant.FAILED;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    @Override
    public int extractInsideFile(List<FileInfo> selectedList, String desFolder, String password,
                                 String charset, Bundle args, ExtractListener listener) {

        int result = ResultConstant.SUCCESS;
        int count = selectedList.size();
        ArrayList<String> selectedPathList = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            selectedPathList.add(ArchiveConstant.getCompressionFileInsidePath(selectedList.get(index).getPath()));
        }

        InputStream inputStream = null;
        if (mZipArchiveEntry == null) {
            mZipArchiveEntry = new ZipArchiveEntry(mFile.getName());
        }
        try {
            inputStream = mZip.getInputStream(mZipArchiveEntry);
            count = selectedPathList.size();
            ZipArchiveEntry entry;
            Enumeration<ZipArchiveEntry> entryEnumeration = mZip.getEntries();
            String name;
            while (entryEnumeration.hasMoreElements()) {
                entry = entryEnumeration.nextElement();
                if (entry == null) {
                    break;
                }

                name = getEntryName(entry);
                for (int index = 0; index < count; index++) {
                    if (name.startsWith(selectedPathList.get(index))) {
                        inputStream = mZip.getInputStream(entry);
                        result = extractApkFileImplement(desFolder, inputStream, entry, name, args, listener);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResultConstant.FAILED;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultConstant.CANCEL;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return result;
    }

    private static int extractApkFileImplement(String desDirPath, InputStream inputStream,
                                               ZipArchiveEntry entry, String name, Bundle args,
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
            } while (left > 0);

            out.flush();
            out.close();
        }

        if (listener != null) {
            listener.onProgress(entry.getCompressedSize());

            listener.finishExtractCallback(true, currentFilePath, name);
        }

        return ResultConstant.SUCCESS;
    }

    @Override
    public String extractFile(String desFolder, String password, String charset, Bundle args) {
        String desFilePath = null;

        int result = ResultConstant.SUCCESS;
        try {
            List<FileInfo> list = new ArrayList<>();
            list.add(this);
            result = extractInsideFile(list, desFolder, password, charset, args, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == ResultConstant.SUCCESS) {
            desFilePath = FileUtils.concatPath(desFolder).concat(mZipArchiveEntry.getName());
        }

        return desFilePath;
    }

    @Override
    public OutputStream getOutputStream() {
        return super.getOutputStream();
    }

    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public boolean isHeader() {
        return !mPath.equals(mFile.getPath());
    }

    @Override
    protected String getArchiveFilePath() {
        return mFile != null ? mFile.getAbsolutePath() : null;
    }

//    class CloseOutputStream extends BufferedOutputStream {
//
//        private String path;
//
//        public CloseOutputStream(OutputStream out, String path) {
//            super(out);
//            this.path = path;
//        }
//
//        @Override
//        public void close() throws IOException {
//            super.close();
//            mFile = new File(path);
//            mZip = new ZipFile(path);
//        }
//    }

}
