package com.grt.daemonw.filelibyary.reflect;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.grt.daemonw.filelibyary.utils.BuildUtils;

import java.lang.reflect.Field;
import java.util.Objects;

public class DiskInfo implements Parcelable {
    public static final String ACTION_DISK_SCANNED =
            "android.os.storage.action.DISK_SCANNED";
    public static final String EXTRA_DISK_ID =
            "android.os.storage.extra.DISK_ID";
    public static final String EXTRA_VOLUME_COUNT =
            "android.os.storage.extra.VOLUME_COUNT";

    public static final int FLAG_ADOPTABLE = 1 << 0;
    public static final int FLAG_DEFAULT_PRIMARY = 1 << 1;
    public static final int FLAG_SD = 1 << 2;
    public static final int FLAG_USB = 1 << 3;

    public final String id;
    public final int flags;
    public long size;
    public String label;
    /**
     * Hacky; don't rely on this count
     */
    public int volumeCount;
    public String sysPath;

    public Object diskInfoObj;

    public DiskInfo(Object diskInfoObj) {
        this.diskInfoObj=diskInfoObj;
        id = (String) getField(diskInfoObj, "id");
        flags = (int) getField(diskInfoObj, "flag");
        size = (long) getField(diskInfoObj, "size");
        label = (String) getField(diskInfoObj, "label");
        volumeCount = (int) getField(diskInfoObj, "volumeCount");
        sysPath = (String) getField(diskInfoObj, "sysPath");
    }

    private Object getField(Object diskInfoObj, String fieldName) {
        Object fieldVal = null;
        try {
            if (BuildUtils.thanNougat()) {
                Field field = diskInfoObj.getClass().getField(fieldName);
                field.setAccessible(true);
                fieldVal = field.get(diskInfoObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldVal;
    }

    public DiskInfo(Parcel parcel) {
        id = parcel.readString();
        flags = parcel.readInt();
        size = parcel.readLong();
        label = parcel.readString();
        volumeCount = parcel.readInt();
        sysPath = parcel.readString();
    }

    public String getId() {
        return id;
    }

    private boolean isInteresting(String label) {
        if (TextUtils.isEmpty(label)) {
            return false;
        }
        if (label.equalsIgnoreCase("ata")) {
            return false;
        }
        if (label.toLowerCase().contains("generic")) {
            return false;
        }
        if (label.toLowerCase().startsWith("usb")) {
            return false;
        }
        if (label.toLowerCase().startsWith("multiple")) {
            return false;
        }
        return true;
    }

    public String getDescription() {
        return (String) ReflectUtil.call(diskInfoObj,"getDescription",true,false);
    }

    public boolean isAdoptable() {
        return (flags & FLAG_ADOPTABLE) != 0;
    }

    public boolean isDefaultPrimary() {
        return (flags & FLAG_DEFAULT_PRIMARY) != 0;
    }

    public boolean isSd() {
        return (flags & FLAG_SD) != 0;
    }

    public boolean isUsb() {
        return (flags & FLAG_USB) != 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DiskInfo{" + id + "}:\n")
                .append("size = ").append(size).append('\n')
                .append("label = ").append(label).append('\n')
                .append("flag = ").append(flags).append('\n')
                .append("sysPath = ").append(sysPath).append('\n');
        return sb.toString();
    }

    @Override
    public DiskInfo clone() {
        final Parcel temp = Parcel.obtain();
        try {
            writeToParcel(temp, 0);
            temp.setDataPosition(0);
            return CREATOR.createFromParcel(temp);
        } finally {
            temp.recycle();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof DiskInfo) {
            return Objects.equals(id, ((DiskInfo) o).id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final Creator<DiskInfo> CREATOR = new Creator<DiskInfo>() {
        @Override
        public DiskInfo createFromParcel(Parcel in) {
            return new DiskInfo(in);
        }

        @Override
        public DiskInfo[] newArray(int size) {
            return new DiskInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeInt(this.flags);
        parcel.writeLong(size);
        parcel.writeString(label);
        parcel.writeInt(volumeCount);
        parcel.writeString(sysPath);
    }
}

