package com.grt.filemanager.util;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.model.ImageSource;
import com.grt.filemanager.model.MusicSource;
import com.grt.filemanager.model.VideoSource;
import com.grt.filemanager.orm.dao.DownloadMusic;
import com.grt.filemanager.orm.dao.FavourateMusic;
import com.grt.filemanager.orm.dao.Music;
import com.grt.filemanager.orm.dao.RecentMusic;
import com.grt.filemanager.orm.dao.base.FavourateMusicDao;
import com.grt.filemanager.orm.helper.DbUtils;
import com.grt.filemanager.orm.helper.implement.DownloadMusicHelper;
import com.grt.filemanager.orm.helper.implement.FavourateMusicHelper;
import com.grt.filemanager.orm.helper.implement.RecentMusicHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MediaHelper {

    private static MediaHelper mediaHelper;

    private MediaMetadataRetriever mRetriever;

    private MediaHelper() {
        mRetriever = new MediaMetadataRetriever();
    }

    public static MediaHelper getMediaHelper() {
        if (mediaHelper == null) {
            mediaHelper = new MediaHelper();
        }
        return mediaHelper;
    }

    /**
     * 查询所有音乐
     *
     * @param context context
     * @return 查询结果
     */
    public Cursor queryAll(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + ")," + " count(*)"};
        return context.getContentResolver().query(uri, projection, null, null, null);
    }


    public Cursor queryMusic(Context context, String path) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.DATA + " = ?";
        String SelectionArgs[] = new String[]{path};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryRing(Context context, String path) {
        Uri uri = MusicSource.MUSIC_INTERNAL_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.DATA + " = ?";
        String SelectionArgs[] = new String[]{path};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryByAlbum(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                + " count(distinct album)"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.MIME_TYPE + " != ? and "
                + MediaStore.Audio.Media.MIME_TYPE + " != ?";
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg", "audio/mp4"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor queryByArtist(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                + " count(distinct artist)"};
        return context.getContentResolver().query(uri, projection, null, null, null);
    }

    public Cursor queryByRing(Context context) {
        Uri uri = MusicSource.MUSIC_INTERNAL_URI;
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                + " count(*)"};
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String selectionArgs[] = new String[]{MusicSource.RINGPATH + "%"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Cursor queryByRecord(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                + " count(*)"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " = ? ";
        String selectionArgs[] = new String[]{"audio/mp4"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    public Music queryByRecent() {
        RecentMusicHelper helper = DbUtils.getRecentMusicHelper();
        List<RecentMusic> list = helper.queryBuilder().list();
        long size = 0;
        int count = 0;

        for (RecentMusic recent : list) {
            String path = recent.getPath();
            if (FileUtils.isFileExist(path)) {
                size = size + recent.getSize();
                count++;
            } else {
                MusicInfoHelper.deleteRecentlyDbCache(recent.getPath());
                MediaUtils.deleteMediaStore(path);
            }
        }
        Music music = new Music();
        music.setSize(size);
        music.setCount(count);
        return music;
    }

    public Music queryByFavourate() {
        FavourateMusicHelper helper = DbUtils.getFavourateMusicHelper();
        List<FavourateMusic> list = helper.queryBuilder().list();
        long size = 0;
        int count = 0;

        for (FavourateMusic favourate : list) {
            String path = favourate.getPath();
            if (FileUtils.isFileExist(path)) {
                size = size + favourate.getSize();
                count++;
            } else {
                MusicInfoHelper.deleteFavourateDbCache(path);
                MediaUtils.deleteMediaStore(path);
            }
        }

        Music music = new Music();
        music.setSize(size);
        music.setCount(count);

        return music;
    }

    public Music queryByDownload() {
        DownloadMusicHelper helper = DbUtils.getDownloadMusicHelper();
        List<DownloadMusic> list = helper.queryBuilder().list();

        long size = 0;
        int count = 0;
        for (DownloadMusic download : list) {
            String path = download.getPath();
            if (FileUtils.isFileExist(path)) {
                size = size + download.getSize();
                count++;
            } else {
                MusicInfoHelper.deleteDownloadDbCache(path);
                MediaUtils.deleteMediaStore(path);
            }
        }

        Music music = new Music();
        music.setSize(size);
        music.setCount(count);

        return music;
    }

    /**
     * 显示所有Music
     *
     * @param context Context
     * @return
     */
    public Cursor showMusic(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.MIME_TYPE + " != ?";
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 根据专辑划分music
     *
     * @param context Context
     * @return
     */
    public Cursor showAlbum(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{MediaStore.Audio.Media.ALBUM +
                ",sum(" + MediaStore.Files.FileColumns.SIZE + ")," + "count()"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ?)" + " and "
                + "(" + MediaStore.Audio.Media.MIME_TYPE + " != ?) " + "GROUP BY (" + MediaStore.Audio.Media.ALBUM;
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 根据歌手划分music
     *
     * @param context Context
     * @return
     */
    public Cursor showArtist(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{MediaStore.Audio.Media.ARTIST +
                ",sum(" + MediaStore.Files.FileColumns.SIZE + ")," + "count()"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ?)" + " and "
                + "(" + MediaStore.Audio.Media.MIME_TYPE + " != ?) " + "GROUP BY (" + MediaStore.Audio.Media.ARTIST;
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 查询所有铃声
     *
     * @param context
     * @return
     */
    public Cursor showRing(Context context) {
        Uri uri = MusicSource.MUSIC_INTERNAL_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.DATA + " like ?";
        String selectionArgs[] = new String[]{MusicSource.RINGPATH + "%"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 查询所有录音
     *
     * @param context
     * @return
     */
    public Cursor showRecord(Context context) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " = ?";
        String selectionArgs[] = new String[]{"audio/mp4"};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 查询专辑下的歌曲
     *
     * @param context
     * @return
     */
    public Cursor showMusicByAlbum(Context context, String album) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.ALBUM + " = ?";
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg", album};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 查询对应歌曲下的歌曲
     *
     * @param context
     * @return
     */
    public Cursor showMusicByArtist(Context context, String artist) {
        Uri uri = MusicSource.MUSIC_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.MIME_TYPE + " != ? and " + MediaStore.Audio.Media.ARTIST + " = ?";
        String selectionArgs[] = new String[]{"application/ogg", "audio/ogg", artist};
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
    }

    /**
     * 查询系统媒体库中所有图片
     *
     * @param context
     * @return
     */
    public Cursor queryImagesFolder(Context context) {
        Uri uri = ImageSource.IMAGE_URI;
        String[] projection = new String[]{MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_ID};
        return context.getContentResolver().query(uri, projection, null, null, null);
    }

    /**
     * @param context
     * @return
     */
    public Cursor queryImagesBucketIDByFilePath(Context context, String filePath) {
        Uri uri = ImageSource.IMAGE_URI;
        String[] projection = new String[]{MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String SelectionArgs[] = new String[]{filePath + "%"};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryImage(Context context, String path) {
        Uri uri = ImageSource.IMAGE_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String SelectionArgs[] = new String[]{path};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryImageList(Context context, long bucketId) {
        Uri uri = ImageSource.IMAGE_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        String SelectionArgs[] = new String[]{bucketId + ""};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryVideoFolder(Context context) {
        Uri uri = VideoSource.VIDEO_URI;
        String[] projection = new String[]{"*"};
        return context.getContentResolver().query(uri, projection, null, null, null);
    }

    public Cursor queryVideo(Context context, String path) {
        Uri uri = VideoSource.VIDEO_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String SelectionArgs[] = new String[]{path};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public Cursor queryVideoList(Context context, String parentPath) {
        Uri uri = VideoSource.VIDEO_URI;
        String[] projection = new String[]{"*"};
        String selection = MediaStore.Video.Media.DATA + " like ?";
        String SelectionArgs[] = new String[]{parentPath + "%"};
        return context.getContentResolver().query(uri, projection, selection, SelectionArgs, null);
    }

    public void deleteMedia(Context context, String path) {
        ContentProviderClient client = context.getContentResolver()
                .acquireContentProviderClient(MediaStore.AUTHORITY);

        File file = new File(path);
        if (!file.exists()) {
            String mimeType = FileUtils.getMiMeType(file.getName());

            Uri tabUri = updateMediaStore(mimeType);
            if (tabUri != null && client != null) {
                try {
                    client.delete(tabUri, MediaStore.Images.Media.DATA + " = ? ",
                            new String[]{path});
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        if (client != null) {
            client.release();
        }
    }

    private Uri updateMediaStore(String mimeType) {
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

    public void scanSystemMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void getMediaExtendInfo(String type, String filePath, Bundle args) {
        if (args == null) {
            return;
        }

        switch (type) {
            case DataConstant.IMAGE_JPEG:
                getJpegExtendInfo(filePath, args);
                break;

            case DataConstant.IMAGE:
                getImageExtendInfo(filePath, args);
                break;

            case DataConstant.MUSIC:
                getMusicExtendInfo(filePath, args);
                break;

            case DataConstant.VIDEO:
                getVideoExtendInfo(filePath, args);
                break;

            default:
                break;
        }
    }

    public static void getJpegExtendInfo(String fileName, Bundle args) {
        try {
            ExifInterface exifInterface = new ExifInterface(fileName);

            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_DATETIME);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_IMAGE_WIDTH);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_IMAGE_LENGTH);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_MAKE);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_MODEL);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_EXPOSURE_TIME);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_ISO);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_FLASH);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_WHITE_BALANCE);
            putJpegStringAttribute(exifInterface, args, ExifInterface.TAG_ORIENTATION);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void putJpegStringAttribute(ExifInterface exifInterface,
                                              Bundle args, String tags) {
        String attribute = exifInterface.getAttribute(tags);
        if (attribute != null) {
            args.putString(tags, attribute);
            Log.e(tags, attribute);
        }
    }

    public void getImageExtendInfo(String path, Bundle args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        args.putInt(DataConstant.HEIGHT, options.outHeight);
        args.putInt(DataConstant.WIDTH, options.outWidth);
    }

    public void getMusicExtendInfo(String path, Bundle args) {
        try {
            mRetriever.setDataSource(path);

            args.putString(DataConstant.BITRATE,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            args.putString(DataConstant.MEDIA_DURATION,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            args.putString(DataConstant.AUTHOR,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
            args.putString(DataConstant.ARTIST,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            args.putString(DataConstant.YEAR,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
            args.putString(DataConstant.TITLE,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
            args.putString(DataConstant.ALBUM,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            args.putString(DataConstant.ALBUMARTIST,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
            args.putBoolean(DataConstant.MUSIC_FAVOURATE, isMusicFavourate(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVideoExtendInfo(String path, Bundle args) {
        try {
            mRetriever.setDataSource(path);

            args.putString(DataConstant.BITRATE,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            args.putString(DataConstant.MEDIA_DURATION,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            args.putString(DataConstant.HEIGHT,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            args.putString(DataConstant.WIDTH,
                    mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取多媒体文件的媒体信息
     *
     * @param path     文件路径
     * @param key      读取媒体信息的关键值
     * @param property 媒体文件属性
     * @return 包含请求媒体信息的Bundle
     */
    public Bundle getExtendInfo(String path, String key, int property) {
        Bundle args = new Bundle();
        try {
            mRetriever.setDataSource(path);
            args.putString(key, mRetriever.extractMetadata(property));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return args;
    }

    private boolean isMusicFavourate(String path) {
        if (null == path || TextUtils.isEmpty(path)) return false;

        FavourateMusicHelper helper = DbUtils.getFavourateMusicHelper();
        long count = helper.queryBuilder().where(FavourateMusicDao.Properties.Path.eq(path)).count();
        if (count > 0) {
            return true;
        }

        return false;
    }
}
