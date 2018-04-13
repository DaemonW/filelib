package com.grt.filemanager.model;

import android.net.Uri;
import android.provider.MediaStore;


import com.grt.filemanager.R;
import com.grt.filemanager.constant.DataConstant;

import java.util.ArrayList;
import java.util.List;

public class VideoSource extends BaseAccount {

    public static final String VIDEO_ROOT_URI = "video_root_uri";
    public static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    @Override
    public List<DataAccountInfo> getAccountList() {
        ArrayList<DataAccountInfo> list = new ArrayList<>();
        list.add(createDataRootInfo(R.string.video,
                DataConstant.VIDEO_DATA_ID, 0, VIDEO_ROOT_URI, 0, 0));
        return list;
    }


}
