package com.grt.daemonw.filelibyary.reflect;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.grt.daemonw.filelibyary.utils.BuildUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Objects;

public class VolumeInfo implements Parcelable {
    public static final String ACTION_VOLUME_STATE_CHANGED =
            "android.os.storage.action.VOLUME_STATE_CHANGED";
    public static final String EXTRA_VOLUME_ID =
            "android.os.storage.extra.VOLUME_ID";
    public static final String EXTRA_VOLUME_STATE =
            "android.os.storage.extra.VOLUME_STATE";

    /**
     * Stub volume representing internal private storage
     */
    public static final String ID_PRIVATE_INTERNAL = "private";
    /**
     * Real volume representing internal emulated storage
     */
    public static final String ID_EMULATED_INTERNAL = "emulated";

    public static final int TYPE_PUBLIC = 0;
    public static final int TYPE_PRIVATE = 1;
    public static final int TYPE_EMULATED = 2;
    public static final int TYPE_ASEC = 3;
    public static final int TYPE_OBB = 4;

    public static final int STATE_UNMOUNTED = 0;
    public static final int STATE_CHECKING = 1;
    public static final int STATE_MOUNTED = 2;
    public static final int STATE_MOUNTED_READ_ONLY = 3;
    public static final int STATE_FORMATTING = 4;
    public static final int STATE_EJECTING = 5;
    public static final int STATE_UNMOUNTABLE = 6;
    public static final int STATE_REMOVED = 7;
    public static final int STATE_BAD_REMOVAL = 8;

    public static final int MOUNT_FLAG_PRIMARY = 1 << 0;
    public static final int MOUNT_FLAG_VISIBLE = 1 << 1;


    /**
     * vold state
     */
    public final String id;
    public final int type;
    public final DiskInfo disk;
    public final String partGuid;
    public int mountFlags = 0;
    public int mountUserId = -1;
    public int state = STATE_UNMOUNTED;
    public String fsType;
    public String fsUuid;
    public String fsLabel;
    public String path;
    public String internalPath;

    public Object volumeInfoObj;

    public VolumeInfo(Object volumeInfoObj) {
        this.volumeInfoObj=volumeInfoObj;
        id = (String) getField(volumeInfoObj, "id");
        Object diskInfoObj=getField(volumeInfoObj,"disk");
        disk=new DiskInfo(diskInfoObj);
        type=(int)getField(volumeInfoObj,"type");
        partGuid=(String)getField(volumeInfoObj,"partGuid");
        mountFlags=(int)getField(volumeInfoObj,"mountFlags");
        mountUserId=(int)getField(volumeInfoObj,"mountUserId");
        state=(int)getField(volumeInfoObj,"state");
        fsType=(String)getField(volumeInfoObj,"fsType");
        fsUuid=(String)getField(volumeInfoObj,"fsUuid");
        fsLabel=(String)getField(volumeInfoObj,"fsLabel");
        internalPath=(String)getField(volumeInfoObj,"internalPath");
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

    public VolumeInfo(Parcel parcel) {
        id = parcel.readString();
        type = parcel.readInt();
        if (parcel.readInt() != 0) {
            disk = DiskInfo.CREATOR.createFromParcel(parcel);
        } else {
            disk = null;
        }
        partGuid = parcel.readString();
        mountFlags = parcel.readInt();
        mountUserId = parcel.readInt();
        state = parcel.readInt();
        fsType = parcel.readString();
        fsUuid = parcel.readString();
        fsLabel = parcel.readString();
        path = parcel.readString();
        internalPath = parcel.readString();
    }

    public String getId() {
        return id;
    }

    public DiskInfo getDisk() {
        return disk;
    }

    public String getDiskId() {
        return (disk != null) ? disk.id : null;
    }

    public int getType() {
        return type;
    }

    public int getState() {
        return state;
    }

    public String getFsUuid() {
        return fsUuid;
    }

    public int getMountUserId() {
        return mountUserId;
    }

    public String getDescription() {
        return (String)ReflectUtil.call(volumeInfoObj,"getDescription",true,false);
    }

    public boolean isMountedReadable() {
        return state == STATE_MOUNTED || state == STATE_MOUNTED_READ_ONLY;
    }

    public boolean isMountedWritable() {
        return state == STATE_MOUNTED;
    }

    public boolean isPrimary() {
        return (mountFlags & MOUNT_FLAG_PRIMARY) != 0;
    }

    public boolean isPrimaryPhysical() {
        return isPrimary() && (getType() == TYPE_PUBLIC);
    }

    public boolean isVisible() {
        return (mountFlags & MOUNT_FLAG_VISIBLE) != 0;
    }

    public boolean isVisibleForRead(int userId) {
        if (type == TYPE_PUBLIC) {
            if (isPrimary() && mountUserId != userId) {
                // Primary physical is only visible to single user
                return false;
            } else {
                return isVisible();
            }
        } else if (type == TYPE_EMULATED) {
            return isVisible();
        } else {
            return false;
        }
    }

    public boolean isVisibleForWrite(int userId) {
        if (type == TYPE_PUBLIC && mountUserId == userId) {
            return isVisible();
        } else if (type == TYPE_EMULATED) {
            return isVisible();
        } else {
            return false;
        }
    }

    public File getPath() {
        return (path != null) ? new File(path) : null;
    }

    public File getInternalPath() {
        return (internalPath != null) ? new File(internalPath) : null;
    }

    @Override
    public VolumeInfo clone() {
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
        if (o instanceof VolumeInfo) {
            return Objects.equals(id, ((VolumeInfo) o).id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final Creator<VolumeInfo> CREATOR = new Creator<VolumeInfo>() {
        @Override
        public VolumeInfo createFromParcel(Parcel in) {
            return new VolumeInfo(in);
        }

        @Override
        public VolumeInfo[] newArray(int size) {
            return new VolumeInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeInt(type);
        if (disk != null) {
            parcel.writeInt(1);
            disk.writeToParcel(parcel, flags);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString(partGuid);
        parcel.writeInt(mountFlags);
        parcel.writeInt(mountUserId);
        parcel.writeInt(state);
        parcel.writeString(fsType);
        parcel.writeString(fsUuid);
        parcel.writeString(fsLabel);
        parcel.writeString(path);
        parcel.writeString(internalPath);
    }
}
