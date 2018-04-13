package com.grt.filemanager.model;

import android.net.Uri;
import android.provider.MediaStore;

import com.grt.filemanager.R;
import com.grt.filemanager.constant.DataConstant;

import java.util.ArrayList;
import java.util.List;

public class ImageSource extends BaseAccount {

    public static final String IMAGE_ROOT_URI = "image_root_uri";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    @Override
    public List<DataAccountInfo> getAccountList() {
        ArrayList<DataAccountInfo> list = new ArrayList<>();
        list.add(createDataRootInfo(R.string.image, DataConstant.IMAGE_DATA_ID, 0, IMAGE_ROOT_URI, 0, 0));
        return list;
    }
}
