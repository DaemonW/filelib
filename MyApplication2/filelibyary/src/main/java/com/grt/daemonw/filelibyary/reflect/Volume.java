package com.grt.daemonw.filelibyary.reflect;

import android.os.Environment;

import com.grt.daemonw.filelibyary.utils.BuildUtils;

import java.io.File;

public class Volume {

    private Object volume;
    public String mPath;
    public int mountType;
    public String mState;
    public String mDescription;

    public Volume(Object volume) {
        this.volume = volume;
        if (BuildUtils.thanMarshmallow()) {
            parseVolumeInfo(volume);
        } else if (BuildUtils.thanLollipop()) {
            parseStorageVolume(volume);
        } else {
            throw new RuntimeException("current os version is not support");
        }
    }


    private void parseStorageVolume(Object obj) {
        File file = (File) ReflectUtil.getField(obj, "mPath");
        mPath = file.getAbsolutePath();
        mState = (String) ReflectUtil.getField(obj, "mState");
        mDescription = (String) ReflectUtil.getField(obj, "mDescription");
    }

    private void parseVolumeInfo(Object obj) {
        mPath = (String) ReflectUtil.getField(obj, "path");
        int state = (int) ReflectUtil.getField(obj, "state");
        switch (state) {
            case 0:
                mState = Environment.MEDIA_UNMOUNTED;
                break;
            case 1:
                mState = Environment.MEDIA_CHECKING;
                break;
            case 2:
                mState = Environment.MEDIA_MOUNTED;
                break;
            case 7:
                mState = Environment.MEDIA_REMOVED;
            default:
                mState = Environment.MEDIA_UNMOUNTED;
                break;
        }
        mDescription = (String) ReflectUtil.call(obj, "getDescription", true, false);
    }
}
