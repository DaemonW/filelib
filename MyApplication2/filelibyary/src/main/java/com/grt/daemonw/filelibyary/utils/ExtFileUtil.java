package com.grt.daemonw.filelibyary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtFileUtil {

    public FileItem fromDocumentFile(DocumentFile file) {
        FileItem item = new FileItem();
        item.file = file;
        item.fileName = file.getName();
        item.lastModified = file.lastModified();
        item.type = file.getType();
        item.parentFile = file.getParentFile();
        item.uri = file.getUri();
        item.size = file.length();
        return item;
    }


    public static DocumentFile getDocumentFile(Context context, String path, Uri rootUri) {
        DocumentFile document = DocumentFile.fromTreeUri(context, rootUri);
        String[] parts = path.split("/");
        for (int i = 3; i < parts.length; i++) {
            document = document.findFile(parts[i]);
        }
        return document;
    }

    public static boolean copyFile(Context context, FileItem srcFileItem, FileItem destFileItem) {
        if (srcFileItem.file.isFile()) {
            OutputStream out = null;
            InputStream in = null;
            ContentResolver resolver = context.getContentResolver();
            try {
                DocumentFile destfile = destFileItem.file.createFile(srcFileItem.file.getType(), srcFileItem.file.getName());
                in = resolver.openInputStream(srcFileItem.uri);
                out = resolver.openOutputStream(destfile.getUri());
                byte[] buf = new byte[64];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return srcFileItem.file.length() == destFileItem.file.length();
        } else {
            try {
                throw new Exception("item is not a file");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public static boolean copyDirectory(Context context, FileItem srcFileItem, FileItem destFileItem) {
        return true;
    }

    public static boolean moveFile(Context context, FileItem srcFileItem, FileItem destFileItem) {
        boolean result = copyFile(context, srcFileItem, destFileItem);
        return result && srcFileItem.file.delete();
    }

    public static boolean moveDirectory(Context context, FileItem srcFileItem, FileItem destFileItem) {
        return true;
    }

    public static boolean delete(FileItem fileItem) {
        return fileItem.file.delete();
    }

    public static boolean rename(FileItem fileItem, String displayName) {
        return fileItem.file.renameTo(displayName);
    }

    public static boolean createFolder(FileItem fileItem, String folderName) {
        return null != fileItem.file.createFile(fileItem.parentFile.getType(), folderName);
    }

    public static boolean createFile(FileItem fileItem, String fileName) {
        return null != fileItem.file.createFile("text/plain", fileName);
    }

    public class FileItem {
        public DocumentFile file;
        public DocumentFile parentFile;
        public Uri uri;
        public String fileName;
        public String filePath;
        public String type;
        public long lastModified;
        public long size;
    }
}
