package com.grt.filemanager.model;

import android.net.Uri;
import android.provider.MediaStore;

import com.grt.filemanager.R;
import com.grt.filemanager.constant.DataConstant;

import java.util.ArrayList;
import java.util.List;

public class MusicSource extends BaseAccount {

    public static final Uri MUSIC_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    public static final Uri MUSIC_INTERNAL_URI = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
    public static final String MUSIC_ROOT_URI = "music_root_uri";
    public static final String RINGPATH = "/system/media/audio/ringtones";

    public static final String MUSIC_URI_QUERY_ALL = "music_uri_query_all";
    public static final String MUSIC_URI_QUERY_ALL_ALBUM = "music_uri_qyery_all_album";
    public static final String MUSIC_URI_QUERY_ALL_ARTIST = "music_uri_query_all_artist";
    public static final String MUSIC_URI_QUERY_ALL_RING = "music_uri_query_all_ring";
    public static final String MUSIC_URI_QUERY_ALL_RECORD = "music_uri_query_all_record";
    public static final String MUSIC_URI_QUERY_MUSIC_BY_ALBUM = "music_uri_query_music_by_album?";
    public static final String MUSIC_URI_QUERY_MUSIC_BY_ARTIST = "music_uri_query_music_by_artist?";
    public static final String MUSIC_URI_QUERY_MUSIC_RECENT = "music_uri_query_music_by_recent?";
    public static final String MUSIC_URI_QUERY_MUSIC_FAVOURATE = "music_uri_query_music_by_favourate?";
    public static final String MUSIC_URI_QUERY_MUSIC_DOWNLOAD = "music_uri_query_music_by_download?";

    private static ArrayList<String> dirPath = new ArrayList<String>(){
        {
            add(MUSIC_ROOT_URI);
            add(MUSIC_URI_QUERY_ALL);
            add(MUSIC_URI_QUERY_ALL_ALBUM);
            add(MUSIC_URI_QUERY_ALL_ARTIST);
            add(MUSIC_URI_QUERY_ALL_RING);
            add(MUSIC_URI_QUERY_ALL_RECORD);
            add(MUSIC_URI_QUERY_MUSIC_RECENT);
            add(MUSIC_URI_QUERY_MUSIC_FAVOURATE);
            add(MUSIC_URI_QUERY_MUSIC_DOWNLOAD);
        }
    };

    public static ArrayList<String> getDirPath() {
        return dirPath;
    }

    @Override
    public List<DataAccountInfo> getAccountList() {
        ArrayList<DataAccountInfo> list = new ArrayList<>();
        list.add(createDataRootInfo(R.string.music, DataConstant.MUSIC_DATA_ID, 0, MUSIC_ROOT_URI, 0, 0));
        return list;
    }
}
