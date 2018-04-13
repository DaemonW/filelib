package com.grt.filemanager.model;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;

public class DirInfo {

    private DirInfo() {
    }

    private static ArrayMap<String, Bundle> dirInfo;

    public static ArrayMap<String, Bundle> getDirInfo() {
        if (dirInfo == null) {
            dirInfo = new ArrayMap<>();
        }
        return dirInfo;
    }
}
