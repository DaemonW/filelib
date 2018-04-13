package com.grt.filemanager.model;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.grt.filemanager.R;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.orm.dao.DownloadMusic;
import com.grt.filemanager.orm.dao.FavourateMusic;
import com.grt.filemanager.orm.dao.Music;
import com.grt.filemanager.orm.dao.RecentMusic;
import com.grt.filemanager.orm.dao.base.RecentMusicDao;
import com.grt.filemanager.orm.helper.DbUtils;
import com.grt.filemanager.orm.helper.implement.DownloadMusicHelper;
import com.grt.filemanager.orm.helper.implement.FavourateMusicHelper;
import com.grt.filemanager.orm.helper.implement.RecentMusicHelper;
import com.grt.filemanager.util.FeThumbUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.MediaHelper;
import com.grt.filemanager.util.MediaUtils;
import com.grt.filemanager.util.MusicInfoHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liucheng on 2015/11/8.
 */
public class MusicObject extends LocalObject {

    private static MediaHelper mMusicHelper = MediaHelper.getMediaHelper();

    private Music mMusic;
    private boolean mCanLongPress;

    public MusicObject(String path) {
        // path==null 因为Mi-498 的bug
        if (path == null || MusicSource.getDirPath().contains(path) ||
                path.contains(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ALBUM) ||
                path.contains(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ARTIST)) {
            this.mMusic = initMusic(path);
        } else {
            Cursor cursor;
            cursor = path.contains(MusicSource.RINGPATH) ? mMusicHelper.queryRing(context, path) :
                    mMusicHelper.queryMusic(context, path);
            if (null != cursor && cursor.moveToFirst()) {
                mMusic = createMusic(cursor);
            } else {
                File file = new File(path);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mMusic = createMusic(file);
            }
        }
    }

    private Music createMusic(File file) {
        Music music = new Music();

        music.setFileName(file.getName());
        music.setFilePath(file.getPath());
        music.setSize(file.length());
        music.setMimeType(FileUtils.getMiMeType(file.getName()));
        music.setLastModify(file.lastModified());
        music.setIsFolder(file.isDirectory());

        long duration = 0;
        Bundle result = mMusicHelper.getExtendInfo(file.getPath(),
                DataConstant.MEDIA_DURATION, MediaMetadataRetriever.METADATA_KEY_DURATION);
        if (result != null) {
            String tmp = result.getString(DataConstant.MEDIA_DURATION);
            if (!TextUtils.isEmpty(tmp)) {
                duration = Long.parseLong(tmp);
            }
        }
        music.setDuration(duration);

        return music;
    }

    public MusicObject(Music music) {
        this.mMusic = music;
    }

    /**
     * 初始化Music
     * 包括根目录
     */          //二级目录  path 为自定义 + 专辑名称或歌手名称
    //name 为专辑名或歌手名  tab使用
    private Music initMusic(String path) {
        Music music = new Music();

        if (path.equals(MusicSource.MUSIC_ROOT_URI)) {
            music.setFileName(context.getResources().getString(R.string.music));
            music.setFilePath(MusicSource.MUSIC_ROOT_URI);
        } else if (path.equals(MusicSource.MUSIC_URI_QUERY_ALL)) {
            music.setFileName(context.getResources().getString(R.string.music_all_music));
        } else if (path.equals(MusicSource.MUSIC_URI_QUERY_ALL_ALBUM)) {
            music.setFileName(context.getResources().getString(R.string.music_album));
        } else if (path.equals(MusicSource.MUSIC_URI_QUERY_ALL_ARTIST)) {
            music.setFileName(context.getResources().getString(R.string.music_artist));
        } else if (path.equals(MusicSource.MUSIC_URI_QUERY_ALL_RING)) {
            music.setFileName(context.getResources().getString(R.string.music_ring));
        } else if (path.equals(MusicSource.MUSIC_URI_QUERY_ALL_RECORD)) {
            music.setFileName(context.getResources().getString(R.string.music_record));
        } else if (path.startsWith(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ALBUM)) {
            String name = path.substring((MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ALBUM).length(),
                    path.length());
            music.setFileName(name);
        } else if (path.startsWith(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ARTIST)) {
            String name = path.substring((MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ARTIST).length(),
                    path.length());
            music.setFileName(name);
        } else if(path.startsWith(MusicSource.MUSIC_URI_QUERY_MUSIC_RECENT)) {
            music.setFileName(context.getResources().getString(R.string.musplayer_recent_play));
        } else if(path.startsWith(MusicSource.MUSIC_URI_QUERY_MUSIC_FAVOURATE)) {
            music.setFileName(context.getResources().getString(R.string.musplayer_my_fav));
        } else if(path.startsWith(MusicSource.MUSIC_URI_QUERY_MUSIC_DOWNLOAD)) {
            music.setFileName(context.getResources().getString(R.string.musplayer_download));
        }
        music.setFilePath(path);
        music.setIsFolder(true);

        return music;
    }

    private Music createMusic(Cursor cursor) {
        Music music = new Music();

        try {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            cursor.close();

            if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(path)) {
                name = FileUtils.getFileName(path);
            }
            music.setFileName(name);
            music.setFilePath(path);
            music.setSize(size);
            music.setMimeType(mime);
            music.setLastModify(time * 1000);
            music.setIsFolder(false);
            music.setDuration(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return music;
    }

    @Override
    public String getName() {
        return mMusic.getFileName();
    }

    @Override
    public String getPath() {
        return mMusic.getFilePath();
    }

    @Override
    public long getLastModified() {
        return mMusic.getLastModify();
    }

    @Override
    public long getSize() {
        return mMusic.getSize();
    }

    @Override
    public boolean isFolder() {
        return mMusic.getIsFolder();
    }

    @Override
    public String getMimeType() {
        return mMusic.getMimeType();
    }

    @Override
    public Bitmap getThumb(boolean isList) {
        return FeThumbUtils.getThumbFromDb(context, FileUtils.getMiMeType(mMusic.getFileName()),
                mMusic.getFilePath(), isList);
    }

    @Override
    public Bundle getSpecialInfo(String type) {
        Bundle bundle = new Bundle();

        switch (type) {
            case DataConstant.ALL_CHILDREN_COUNT:
                bundle.putInt(type, mMusic.getCount());
                break;

            case DataConstant.MEDIA_DURATION:
                bundle.putLong(type, mMusic.getDuration());
                break;

            case DataConstant.OPERATION_PERMISSION:
                if (mCanLongPress) {
                    bundle.putString(type, DataConstant.buildOperationPermission(!mMusic.getIsFolder(), false, true));
                } else {
                    bundle.putString(type, DataConstant.buildOperationPermission(!mMusic.getIsFolder(), false, false));
                }
                break;

            default:
                MediaHelper.getMediaHelper().getMediaExtendInfo(type, mMusic.getFilePath(), bundle);
                break;
        }

        return bundle;
    }

    @Override
    public List<FileInfo> getList(boolean containHide) {
        List<FileInfo> infoList = new ArrayList<>();

        if (null != mMusic && mMusic.getFilePath().equals(MusicSource.MUSIC_ROOT_URI)) {
            showMusicFolder(infoList);
        } else {
            ArrayList<Music> musicList = new ArrayList<>();

            queryFromDb(musicList);

            for (Music music : musicList) {
                MusicObject object = new MusicObject(music);
                infoList.add(object);
            }
        }

        return infoList;
    }

    /**
     * 音乐分类目录查询
     */
    private void showMusicFolder(List<FileInfo> fileInfoList) {
        Cursor cursor;
        Music music;
        String tabTitle = null;
        String path = null;
        long size = 0;
        int count = 0;

        for (int i = 0; i <= 7; i++) {
            mCanLongPress = false;
            switch (i) {
                case 0:
                    tabTitle = context.getResources().getString(R.string.music_all_music);
                    path = MusicSource.MUSIC_URI_QUERY_ALL;
                    cursor = mMusicHelper.queryAll(context);
                    if (null != cursor && cursor.moveToFirst()) {
                        size = cursor.getLong(0);
                        count = cursor.getInt(1);
                        cursor.close();
                    }
                    break;

                case 1:
                    tabTitle = context.getResources().getString(R.string.music_album);
                    path = MusicSource.MUSIC_URI_QUERY_ALL_ALBUM;
                    cursor = mMusicHelper.queryByAlbum(context);
                    if (null != cursor && cursor.moveToFirst()) {
                        size = cursor.getLong(0);
                        count = cursor.getInt(1);
                        cursor.close();
                    }
                    break;

                case 2:
                    tabTitle = context.getResources().getString(R.string.music_artist);
                    path = MusicSource.MUSIC_URI_QUERY_ALL_ARTIST;
                    cursor = mMusicHelper.queryByArtist(context);
                    if (null != cursor && cursor.moveToFirst()) {
                        size = cursor.getLong(0);
                        count = cursor.getInt(1);
                        cursor.close();
                    }
                    break;

                case 3:
                    tabTitle = context.getResources().getString(R.string.music_ring);
                    path = MusicSource.MUSIC_URI_QUERY_ALL_RING;
                    cursor = mMusicHelper.queryByRing(context);
                    if (null != cursor && cursor.moveToFirst()) {
                        size = cursor.getLong(0);
                        count = cursor.getInt(1);
                        cursor.close();
                    }
                    break;

                case 4:
                    tabTitle = context.getResources().getString(R.string.music_record);
                    path = MusicSource.MUSIC_URI_QUERY_ALL_RECORD;
                    cursor = mMusicHelper.queryByRecord(context);
                    if (null != cursor && cursor.moveToFirst()) {
                        size = cursor.getLong(0);
                        count = cursor.getInt(1);
                        cursor.close();
                    }
                    break;

                case 5:
                    tabTitle = context.getResources().getString(R.string.musplayer_recent_play);
                    path = MusicSource.MUSIC_URI_QUERY_MUSIC_RECENT;
                    Music result = mMusicHelper.queryByRecent();
                    size = result.getSize();
                    count = result.getCount();
                    break;

                case 6:
                    tabTitle = context.getResources().getString(R.string.musplayer_my_fav);
                    path = MusicSource.MUSIC_URI_QUERY_MUSIC_FAVOURATE;
                    Music favourate = mMusicHelper.queryByFavourate();
                    size = favourate.getSize();
                    count = favourate.getCount();
                    break;

                case 7:
                    tabTitle = context.getResources().getString(R.string.musplayer_download);
                    path = MusicSource.MUSIC_URI_QUERY_MUSIC_DOWNLOAD;
                    Music download = mMusicHelper.queryByDownload();
                    size = download.getSize();
                    count = download.getCount();
                    break;

                default:
                    break;


            }

            music = new Music();

            music.setFileName(tabTitle);
            music.setFilePath(path);
            music.setSize(size);
            music.setCount(count);
            music.setIsFolder(true);

            MusicObject musicObject = new MusicObject(music);
            fileInfoList.add(musicObject);
        }
    }

    /**
     * 根据分类要求查询
     */
    private void queryFromDb(ArrayList<Music> musicList) {
        Cursor cursor;
        if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_ALL)) {
            mCanLongPress = true;
            cursor = mMusicHelper.showMusic(context);
            getMusicFileList(cursor, musicList);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_ALL_ALBUM)) {
            mCanLongPress = false;
            cursor = mMusicHelper.showAlbum(context);
            getMusicFolderFileList(cursor, musicList, true);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_ALL_ARTIST)) {
            mCanLongPress = false;
            cursor = mMusicHelper.showArtist(context);
            getMusicFolderFileList(cursor, musicList, false);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_ALL_RING)) {
            mCanLongPress = true;
            cursor = mMusicHelper.showRing(context);
            getMusicFileList(cursor, musicList);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_ALL_RECORD)) {
            mCanLongPress = true;
            cursor = mMusicHelper.showRecord(context);
            getMusicFileList(cursor, musicList);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ALBUM + mMusic.getFileName())) {
            mCanLongPress = true;
            cursor = mMusicHelper.showMusicByAlbum(context, mMusic.getFileName());
            getMusicFileList(cursor, musicList);
            cursor.close();
        } else if (mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ARTIST + mMusic.getFileName())) {
            mCanLongPress = true;
            cursor = mMusicHelper.showMusicByArtist(context, mMusic.getFileName());
            getMusicFileList(cursor, musicList);
            cursor.close();
        } else if(mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_MUSIC_RECENT)) {
            mCanLongPress = true;
            getRecentMusicList(musicList);
        } else if(mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_MUSIC_FAVOURATE)) {
            mCanLongPress = true;
            getFavourateMusicList(musicList);
        } else if(mMusic.getFilePath().equals(MusicSource.MUSIC_URI_QUERY_MUSIC_DOWNLOAD)) {
            mCanLongPress = true;
            getDownloadMusicList(musicList);
        }
    }

    /**
     * 专辑  歌手 二级目录
     */
    private void getMusicFolderFileList(Cursor cursor, ArrayList<Music> musicList,
                                        boolean isAlbum) {
        if (cursor.moveToFirst()) {
            Music music;

            do {
                music = new Music();
                String name = cursor.getString(0);
                String path = isAlbum ? MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ALBUM :
                        MusicSource.MUSIC_URI_QUERY_MUSIC_BY_ARTIST;
                path = path + name;
                long size = cursor.getLong(1);
                int count = cursor.getInt(2);

                music.setFileName(name);
                music.setFilePath(path);
                music.setIsFolder(true);
                music.setSize(size);
                music.setCount(count);

                musicList.add(music);
            } while (cursor.moveToNext());
        }
    }

    /**
     * 歌曲列表
     */
    private void getMusicFileList(Cursor cursor, ArrayList<Music> musicList) {
        if (cursor.moveToFirst()) {
            Music music;
            do {
                music = new Music();

                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String mime = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
                long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(path)) {
                    name = FileUtils.getFileName(path);
                }
                music.setFileName(name);
                music.setFilePath(path);
                music.setSize(size);
                music.setMimeType(mime);
                music.setLastModify(time * 1000);
                music.setIsFolder(false);
                music.setDuration(duration);
                musicList.add(music);
            } while (cursor.moveToNext());
        }
    }

    /**
     * 最近播放列表
     * @param musicList
     */
    private void getRecentMusicList(ArrayList<Music> musicList) {
        RecentMusicHelper helper = DbUtils.getRecentMusicHelper();
        //按照播放时间降序排列
        List<RecentMusic> list = helper.queryBuilder().orderDesc(RecentMusicDao.Properties.ClickTime).list();
        String path;
        for(RecentMusic recent : list) {
            path = recent.getPath();
            if(FileUtils.isFileExist(path)) {
                Music music = new Music();
                music.setFileName(recent.getName());
                music.setFilePath(recent.getPath());
                music.setAlbum(recent.getAlbum());
                music.setSize(recent.getSize());
                music.setMimeType(recent.getMimeType());
                music.setLastModify(recent.getLastModified());
                if(null != recent.getDuration()) {
                    music.setDuration(recent.getDuration());
                } else {
                    music.setDuration(0);
                }
                music.setIsFolder(false);
                musicList.add(music);
            } else {
                MusicInfoHelper.deleteRecentlyDbCache(path);
                MediaUtils.deleteMediaStore(path);
            }
        }
    }

    /**
     * 收藏列表
     * @param musicList
     */
    private void getFavourateMusicList(ArrayList<Music> musicList) {
        FavourateMusicHelper helper = DbUtils.getFavourateMusicHelper();
        List<FavourateMusic> list = helper.queryBuilder().list();
        String path;
        for(FavourateMusic favourate : list) {
            path = favourate.getPath();
            if(FileUtils.isFileExist(path)) {
                Music music = new Music();
                music.setFileName(favourate.getName());
                music.setFilePath(favourate.getPath());
                music.setAlbum(favourate.getAlbum());
                music.setSize(favourate.getSize());
                music.setMimeType(favourate.getMimeType());
                music.setLastModify(favourate.getLastModified());
                music.setDuration(favourate.getDuration());
                music.setIsFolder(false);
                musicList.add(music);
            } else {
                MusicInfoHelper.deleteFavourateDbCache(path);
                MediaUtils.deleteMediaStore(path);
            }
        }
    }

    /**
     * 下载列表
     * @param musicList
     */
    private void getDownloadMusicList(ArrayList<Music> musicList) {
        DownloadMusicHelper helper = DbUtils.getDownloadMusicHelper();
        List<DownloadMusic> list = helper.queryBuilder().list();
        String path;
        for(DownloadMusic download : list) {
            path = download.getPath();
            if(FileUtils.isFileExist(path)) {
                Music music = new Music();
                music.setFileName(download.getName());
                music.setFilePath(download.getPath());
                music.setAlbum(download.getAlbum());
                music.setSize(download.getSize());
                music.setMimeType(download.getMimeType());
                music.setLastModify(download.getLastModified());
                music.setDuration(download.getDuration());
                music.setIsFolder(false);
                musicList.add(music);
            } else {
                MusicInfoHelper.deleteDownloadDbCache(path);
                MediaUtils.deleteMediaStore(path);
            }
        }
    }

    @Override
    protected String getFilePath() {
        return mMusic.getFilePath();
    }

    @Override
    public int create(boolean isFolder) {
        return 0;
    }

    @Override
    public boolean rename(String newPath) {
        boolean result = super.rename(newPath);

        if (result) {
            mMusic.setFileName(FileUtils.getFileName(newPath));
            mMusic.setFilePath(newPath);
        }

        return result;
    }

}
