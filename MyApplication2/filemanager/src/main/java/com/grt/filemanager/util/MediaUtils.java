package com.grt.filemanager.util;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;


import com.grt.filemanager.apientrance.ApiPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MediaUtils {

    private MediaUtils() {
    }

    private static ContentProviderClient client = ApiPresenter.getContext()
            .getContentResolver().acquireContentProviderClient(MediaStore.AUTHORITY);

    private static Context context = ApiPresenter.getContext();

    public static ContentProviderClient getClient() {
        return client;
    }

    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR =
            "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

//    public static void scanAllMedia(Context context) {
//        List<File> storageList = StorageUtils.getAllStorage();
//        for (File file : storageList) {
//            updateFileTable(context, file);
//        }
//    }

    //filename是我们的文件全名，包括后缀
    public static void updateSystemMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
//                        Log.d("scanFile", "Scanned " + path + ":");
//                        Log.d("scanFile", "-> uri=" + uri);
                    }
                });
    }

    public static void updateSystemMedia(Context context, String[] paths) {
        MediaScannerConnection.scanFile(context, paths, null, null);
    }

    public static void updateSystemMedia(String path) {
        if (context != null) {
            MediaScannerConnection.scanFile(context, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        }
    }

    public static void updateSystemMedia(String[] path) {
        if (path == null) {
            return;
        }

        try {
            MediaScannerConnection.scanFile(context, path, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateSystemFolder(Context context, String path) {
        File file = new File(path);
        if (file.isFile()) {
            updateSystemMedia(context, path);
        } else {
            ConcurrentLinkedQueue<File> qualifiedFileQueue = new ConcurrentLinkedQueue<>();
            List<String> fileList = new ArrayList<>();

            getFilePaths(file, qualifiedFileQueue, fileList);

            if (fileList.size() <= 0) {
                return;
            }
            String[] paths = new String[fileList.size()];
            fileList.toArray(paths);
            updateSystemMedia(context, paths);
        }
    }

    public static void getFilePaths(File file, ConcurrentLinkedQueue<File> qualifiedFileQueue,
                                    List<String> fileLists) {
        File[] files = file.listFiles();

        if (files != null) {
            Collections.addAll(qualifiedFileQueue, files);

            if (file.isFile()) {
                fileLists.add(file.getPath());
            }

            while (!qualifiedFileQueue.isEmpty()) {
                File childFile = qualifiedFileQueue.poll();
                if (childFile == null)
                    continue;

                File[] childrenFiles = childFile.listFiles();
                if (childFile.isFile()) {
                    fileLists.add(childFile.getPath());
                }

                if (null == childrenFiles || childrenFiles.length == 0) {
                    continue;
                }

                Collections.addAll(qualifiedFileQueue, childrenFiles);
            }
        }
    }

    public static void renameMediaStore(String path, String newPath) {
        File file = new File(path);
        if (!file.exists()) {
            String mimeType = FileUtils.getMiMeType(file.getName());

            Uri tabUri = getMediaUri(mimeType);
            if (tabUri != null && client != null) {
                try {
                    ContentValues cv = new ContentValues();
                    cv.put(MediaStore.Images.Media.DATA, newPath);
                    client.update(tabUri, cv, MediaStore.Images.Media.DATA + "= ?",
                            new String[]{path});
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteMediaStore(String path) {
        File file = new File(path);
        if (!file.exists()) {
            String mimeType = FileUtils.getMiMeType(file.getName());

            Uri tabUri = getMediaUri(mimeType);
            if (tabUri != null && client != null) {
                try {
                    if(FileUtils.isAudio(mimeType)) {
                        client.delete(tabUri, MediaStore.Audio.Media.DATA + " = ?",
                                new String[]{path});
                        Log.d("MediaUtils", "media delete music succeed");
                    } else {
                        client.delete(tabUri, MediaStore.Images.Media.DATA + " = ?",
                                new String[]{path});
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void directDelMediaStore(String path) {
        String mimeType = FileUtils.getMiMeType(FileUtils.getFileName(path));

        Uri tabUri = getMediaUri(mimeType);
        if (tabUri != null && client != null) {
            try {
                client.delete(tabUri, MediaStore.Images.Media.DATA + " = ?", new String[]{path});
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri getMediaUri(String mimeType) {
        Uri tabUri = null;

        if (mimeType.startsWith("audio") || mimeType.equals("application/ogg")) {
            tabUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("image")) {
            tabUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mimeType.startsWith("video")) {
            tabUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        return tabUri;
    }

    public static void deleteSystemFileDb(String path) {
        Uri uri = MediaStore.Files.getContentUri("external");
        String where = MediaStore.Files.FileColumns.DATA + " = ?";
        String[] selectionArgs = new String[]{path};

        try {
            context.getContentResolver().delete(uri, where, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
