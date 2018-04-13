package com.grt.daemonw.filelibyary.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.grt.daemonw.filelibyary.reflect.ReflectUtil;
import com.grt.daemonw.filelibyary.reflect.Volume;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageUtil {

    public static List<Volume> getVolumes(Context context) {
        List<? extends Object> volumeList = getVolumeList(context);
        List<Volume> volumes = new ArrayList<>();
        for (Object v : volumeList) {
            volumes.add(new Volume(v));
        }
        return volumes;
    }

    private static List<StorageVolume> getVolumeList(StorageManager sm) {
        List<StorageVolume> volumeList = new ArrayList<>();
        if (BuildUtils.thanNougat()) {
            volumeList = sm.getStorageVolumes();
        } else if (BuildUtils.thanMarshmallow()) {
            volumeList = getVolumeListFromReflect(sm);
        } else if(BuildUtils.thanLollipop()){
            //TODO: compatible with low version android os

        }
        return volumeList;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static List<StorageVolume> getVolumeListFromReflect(StorageManager sm) {
        List<StorageVolume> volumes = null;
        try {
            Method method = StorageManager.class.getDeclaredMethod("getVolumeList");
            StorageVolume[] result = (StorageVolume[]) method.invoke(sm);
            if (result != null && result.length > 0) {
                volumes = new ArrayList<>();
                Collections.addAll(volumes, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volumes;
    }

    public static List<? extends Object> getVolumeList(Context context) {
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        if (sm == null) {
            return null;
        }
        List<? extends Object> volumeList = new ArrayList<>();
        if (BuildUtils.thanMarshmallow()) {
            volumeList = (List<Object>) ReflectUtil.call(sm, "getVolumes", true, false);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            volumeList = getVolumeList(sm);
        } else {
            //TODO: compatible with low version android os
        }
        return volumeList;
    }
}
