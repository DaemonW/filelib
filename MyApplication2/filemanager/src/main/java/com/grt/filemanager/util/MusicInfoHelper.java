package com.grt.filemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.MusicObject;
import com.grt.filemanager.orm.dao.DownloadMusic;
import com.grt.filemanager.orm.dao.FavourateMusic;
import com.grt.filemanager.orm.dao.RecentMusic;
import com.grt.filemanager.orm.dao.base.DownloadMusicDao;
import com.grt.filemanager.orm.dao.base.FavourateMusicDao;
import com.grt.filemanager.orm.dao.base.RecentMusicDao;
import com.grt.filemanager.orm.helper.DbUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:
 * Added by yinyong on 2016/7/28.
 */
public class MusicInfoHelper {
    /**
     * 用户点击文件所在目录的媒体文件列表
     */
    private static final List<FileInfo> mediaInfos = new ArrayList<>();
    /**
     * 文件所属类型为本地文件
     */
    public static int MUSIC_INFO_TYPE_LOCAL = 0;
    /**
     * 文件所属类型为网盘文件
     */
    public static int MUSIC_INFO_TYPE_NET = 1;
    /**
     * 文件所属类型为USB文件
     */
    public static int MUSIC_INFO_TYPE_USB = 2;
    private static String TAG = "MusicInfoHelper";

    /**
     * 列表循环
     */
    public static final int MUSIC_MODE_REPEAT_LIST = 6;
    /**
     * 单曲循环
     */
    public static final int MUSIC_MODE_REPEAT_ONE = 7;
    /**
     * 随机播放
     */
    public static final int MUSIC_MODE_SHUFFLE = 8;

    private static int mediaRepeatMode = MUSIC_MODE_REPEAT_LIST;

    /**
     * 当前播放文件所属类型
     */
    private static int mediaType = MUSIC_INFO_TYPE_LOCAL;
    /**
     * 用户点击文件所处列表位置
     */
    private static int mediaPosition = 0;
    /**
     * 音乐查询路径
     */
    private static String mediaQueryPath;
    /**
     * 音乐侧滑栏分类ID
     */
    private static int mediaQueryId;

    /**
     * 设置点击媒体文件列表
     *
     * @param type  文件类型
     * @param info  文件信息
     * @param infos 文件列表信息
     */
    public synchronized static void setFileInfos(final int type, final FileInfo info, final List<FileInfo> infos) {
        if (null == info || null == infos || infos.size() <= 0) return;

        mediaType = type;
        synchronized (mediaInfos) {
            mediaInfos.clear();
            boolean isChecked = false;

            for (FileInfo tmpInfo : infos) {
                if (!tmpInfo.isFolder() && (FileUtils.isAudio(tmpInfo.getMimeType())
                        || FileUtils.isAudio(FileUtils.getMiMeType(tmpInfo.getName())))) {

                    mediaInfos.add(tmpInfo);
                    if(!isChecked && info.getPath().equals(tmpInfo.getPath())) {
                        mediaPosition = mediaInfos.size() - 1;
                        isChecked = true;
                    }
                }
            }
        }
    }

    /**
     * 获取当前正在播放的媒体文件
     *
     * @return 文件信息
     */
    public synchronized static FileInfo getMediaInfo() {
        synchronized (mediaInfos) {
            if (mediaPosition < mediaInfos.size() && mediaPosition >= 0) {
                return mediaInfos.get(mediaPosition);
            }
        }
        return null;
    }

    /**
     * 获取当前正在播放的USB媒体文件
     *
     * @return 文件信息
     */
    public static FileInfo getMediaUsbInfo() {
        FileInfo fileInfo = MusicInfoHelper.getMediaInfo();
        if (null != fileInfo && LocalFileHelper.inUsbStorage(fileInfo.getPath())) {
            return fileInfo;
        }
        return null;
    }

    /**
     * 获取媒体文件列表
     *
     * @return 列表
     */
    public synchronized static List<FileInfo> getMediaInfos() {
        synchronized (mediaInfos) {
            return mediaInfos;
        }
    }

    /**
     * 获取视频所在列表位置
     *
     * @return 列表位置
     */
    public static int getMediaPosition() {
        return mediaPosition;
    }

    /**
     * 设置将要播放的媒体文件所处列表位置
     */
    public static void setMediaPosition(int position) {
        mediaPosition = position;
    }

    /**
     * 当前播放文件是否为网盘播放
     */
    public static boolean isCurNetInfo() {
        return mediaType == MUSIC_INFO_TYPE_NET;
    }

    public static boolean isCurUsbInfo() {
        return mediaType == MUSIC_INFO_TYPE_USB;
    }

    /**
     * 移除指定位置文件
     *
     * @param position 需要移除的位置
     */
    public synchronized static void remove(int position) {
        synchronized (mediaInfos) {
            if (position >= 0 && position <= mediaInfos.size() - 1) {
                mediaInfos.remove(position);
            }
        }
    }

    public synchronized static int previous() {
        synchronized (mediaInfos) {
            int size = mediaInfos.size();
            if(size <= 0 || mediaPosition > size - 1) return -1;

            if((mediaPosition < size) && mediaPosition > 0) {
                -- mediaPosition;
            } else {
                mediaPosition = mediaInfos.size() - 1;
            }

            return mediaPosition;
        }
    }

    public synchronized static int next() {
        synchronized (mediaInfos) {
            int size = mediaInfos.size();
            if(mediaInfos.size() <= 0 || mediaPosition > size - 1) return -1;

            if((mediaPosition < size - 1) && mediaPosition >= 0) {
                ++ mediaPosition;
            } else {
                mediaPosition = 0;
            }

            return mediaPosition;
        }
    }

    /**
     * 设置为收藏
     */
    public static boolean setFavourate(FileInfo info) {
        SpecialInfo specialInfo = getSpecialInfo(info);

        try {
            FavourateMusic favourate = new FavourateMusic();
            favourate.setName(info.getName());
            favourate.setPath(info.getPath());
            favourate.setIsFolder(false);
            favourate.setLastModified(info.getLastModified());
            favourate.setSize(info.getSize());
            favourate.setMimeType(info.getMimeType());
            favourate.setClickTime(TimeUtils.getCurrentTime());
            favourate.setDuration(specialInfo.duration);
            favourate.setAlbum(specialInfo.album);
            DbUtils.getFavourateMusicHelper().saveOrUpdate(favourate);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setFavourate", e);
        }
        return false;
    }

    /**
     * 删除收藏记录
     */
    public static boolean removeFavourate(FileInfo info) {
        try {
            DbUtils.getFavourateMusicHelper().queryBuilder().where(FavourateMusicDao.Properties.Path.eq(info.getPath()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "removeFavourate", e);
        }
        return false;
    }

    /**
     * 判断音乐文件是否已收藏
     */
    public static boolean isFavourate(FileInfo info) {
        if (null == info) return false;

        String path = info.getPath();
        if (null == path || TextUtils.isEmpty(path)) return false;

        long count = DbUtils.getFavourateMusicHelper().queryBuilder().where(FavourateMusicDao.Properties.Path.eq(path)).count();
        return count > 0;
    }

    public static boolean isRecent(FileInfo info) {
        if (null == info) return false;

        String path = info.getPath();
        if (null == path || TextUtils.isEmpty(path)) return false;

        long count = DbUtils.getRecentMusicHelper().queryBuilder().where(RecentMusicDao.Properties.Path.eq(path)).count();
        return count > 0;
    }

    public static boolean isDownload(FileInfo info) {
        if (null == info) return false;

        String path = info.getPath();
        if (null == path || TextUtils.isEmpty(path)) return false;

        long count = DbUtils.getDownloadMusicHelper().queryBuilder().where(DownloadMusicDao.Properties.Path.eq(path)).count();
        return count > 0;
    }

    public static void renameFavourate(FileInfo info, String newName) {
        SpecialInfo specialInfo = getSpecialInfo(info);

        if (isFavourate(info)) {
            removeFavourate(info);

            try {
                String newPath = FileUtils.getParentPath(info.getPath()) + "/" + newName;
                FavourateMusic favourateMusic = new FavourateMusic();
                favourateMusic.setName(newName);
                favourateMusic.setPath(newPath);
                favourateMusic.setIsFolder(false);
                favourateMusic.setLastModified(info.getLastModified());
                favourateMusic.setSize(info.getSize());
                favourateMusic.setMimeType(info.getMimeType());
                favourateMusic.setClickTime(TimeUtils.getCurrentTime());
                favourateMusic.setDuration(specialInfo.duration);
                favourateMusic.setAlbum(specialInfo.album);
                DbUtils.getFavourateMusicHelper().saveOrUpdate(favourateMusic);
            } catch (Exception e) {
                Log.e(TAG, "renameDownload", e);
            }
        }
    }

    public static void renameDownload(FileInfo info, String newName) {
        SpecialInfo specialInfo = getSpecialInfo(info);

        if (isDownload(info)) {
            DbUtils.getDownloadMusicHelper().queryBuilder().where(DownloadMusicDao.Properties.Path.eq(info.getPath()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();

            try {
                String newPath = FileUtils.getParentPath(info.getPath()) + "/" + newName;
                DownloadMusic downloadMusic = new DownloadMusic();
                downloadMusic.setName(newName);
                downloadMusic.setPath(newPath);
                downloadMusic.setIsFolder(false);
                downloadMusic.setLastModified(info.getLastModified());
                downloadMusic.setSize(info.getSize());
                downloadMusic.setMimeType(info.getMimeType());
                downloadMusic.setClickTime(TimeUtils.getCurrentTime());
                downloadMusic.setDuration(specialInfo.duration);
                downloadMusic.setAlbum(specialInfo.album);
                DbUtils.getDownloadMusicHelper().saveOrUpdate(downloadMusic);
            } catch (Exception e) {
                Log.e(TAG, "renameDownload", e);
            }
        }
    }

    public static void renameRecent(FileInfo info, String newName) {
        SpecialInfo specialInfo = getSpecialInfo(info);

        if (isRecent(info)) {
            DbUtils.getRecentMusicHelper().queryBuilder().where(RecentMusicDao.Properties.Path.eq(info.getPath()))
                    .buildDelete().executeDeleteWithoutDetachingEntities();

            try {
                String newPath = FileUtils.getParentPath(info.getPath()) + "/" + newName;
                RecentMusic recentMusic = new RecentMusic();
                recentMusic.setName(newName);
                recentMusic.setPath(newPath);
                recentMusic.setIsFolder(false);
                recentMusic.setLastModified(info.getLastModified());
                recentMusic.setSize(info.getSize());
                recentMusic.setMimeType(info.getMimeType());
                recentMusic.setClickTime(TimeUtils.getCurrentTime());
                recentMusic.setDuration(specialInfo.duration);
                recentMusic.setAlbum(specialInfo.album);
                DbUtils.getRecentMusicHelper().saveOrUpdate(recentMusic);
            } catch (Exception e) {
                Log.e(TAG, "renameDownload", e);
            }
        }
    }

    /**
     * 清除磁盘上数据库保存的文件信息
     *
     * @param path 需要清除的文件路径
     */
    public static void clearDbCache(String path) {
        deleteDownloadDbCache(path);
        deleteRecentlyDbCache(path);
        deleteFavourateDbCache(path);
    }

    /**
     * 删除收藏记录
     */
    public static void deleteFavourateDbCache(String path) {
        try {
            DbUtils.getFavourateMusicHelper().queryBuilder().where(FavourateMusicDao.Properties.Path.eq(path))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除最近播放记录
     */
    public static void deleteRecentlyDbCache(String path) {
        try {
            DbUtils.getRecentMusicHelper().queryBuilder().where(RecentMusicDao.Properties.Path.eq(path))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除网络下载记录
     */
    public static void deleteDownloadDbCache(String path) {
        try {
            DbUtils.getDownloadMusicHelper().queryBuilder().where(DownloadMusicDao.Properties.Path.eq(path))
                    .buildDelete().executeDeleteWithoutDetachingEntities();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存当前播放文件路径
     */
    public static void savePlayingFilePath(Context context, String path) {
        SharedPreferences sp = context.getSharedPreferences(DataConstant.MUSIC_PLAYING_MEDIA_PATH, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("playing_path", path);
        ed.apply();
    }

    /**
     * 获取当前播放文件路径
     */
    public static String getPlayingFilePath(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DataConstant.MUSIC_PLAYING_MEDIA_PATH, Context.MODE_PRIVATE);
        return sp.getString("playing_path", "");
    }

    private static SpecialInfo getSpecialInfo(FileInfo info) {
        SpecialInfo specialInfo = new SpecialInfo();
        String album = "Unknown";
        long duration = 0;
        Object albumObj, durationObj;

        if (info instanceof MusicObject) {
            albumObj = info.getSpecialInfo(DataConstant.MUSIC).get(DataConstant.ALBUM);
            durationObj = info.getSpecialInfo(DataConstant.MEDIA_DURATION).get(DataConstant.MEDIA_DURATION);
        } else {
            Bundle bundle = new Bundle();
            //本地查询
            MediaHelper.getMediaHelper().getMusicExtendInfo(info.getPath(), bundle);
            albumObj = bundle.get(DataConstant.ALBUM);
            durationObj = bundle.get(DataConstant.MEDIA_DURATION);
        }

        if (null != albumObj && !TextUtils.isEmpty(albumObj.toString())) {
            album = albumObj.toString();
        }

        if (null != durationObj && !TextUtils.isEmpty(durationObj.toString())) {
            try {
                duration = Long.parseLong(durationObj.toString());
            } catch (Exception e) {
                Log.e(TAG, "getSpecialInfo", e);
            }
        }

        specialInfo.album = album;
        specialInfo.duration = duration;
        return specialInfo;
    }

    public static void setMediaQueryInfo(int dataId, String mediaQueryPath) {
        MusicInfoHelper.mediaQueryId = dataId;
        MusicInfoHelper.mediaQueryPath = mediaQueryPath;
    }

    public static int getMediaRepeatMode() {
        return mediaRepeatMode;
    }

    public static void setMediaRepeatMode(int mediaRepeatMode) {
        MusicInfoHelper.mediaRepeatMode = mediaRepeatMode;
    }

    public static class SpecialInfo {
        String album;
        long duration;
    }
}
