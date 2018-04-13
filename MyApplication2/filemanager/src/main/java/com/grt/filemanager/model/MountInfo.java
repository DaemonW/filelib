package com.grt.filemanager.model;

import android.text.TextUtils;

import com.grt.filemanager.util.ShellUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MountInfo {
    /**
     * The mounted device (can be "none" or any arbitrary string for virtual
     * file systems).
     */
    public String device;
    /**
     * The path where the file system is mounted.
     */
    public String mountpoint;
    /**
     * The file system.
     */
    public String fs;
    /**
     * The mount options. For most file systems, you can use
     */
    public String options;
    /**
     * The dumping frequency for dump(8); see fstab(5).
     */
    public int fs_freq;
    /**
     * The order in which file system checks are done at reboot time; see
     * fstab(5).
     */
    public int fs_passno;

    /**
     * Parses /proc/mounts and gets the mounts.
     *
     * @return the mounts
     */
    public static List<MountInfo> getMounts() {

        String busyBoxPath = ShellUtils.exists(ShellUtils.BUSY_BOX_PATH) ?
                ShellUtils.BUSY_BOX_PATH + " " : "";

        ShellUtils.CommandResult result = ShellUtils.execCommand(busyBoxPath + "cat /proc/mounts", true, true);
        if (!TextUtils.isEmpty(result.successMsg)) {
            return MountInfo.getMounts(result.successMsg);
        }

        return new ArrayList<>();
    }

    public static String getMountStr() {
        String busyBoxPath = ShellUtils.existsWithSh(ShellUtils.BUSY_BOX_PATH) ?
                ShellUtils.BUSY_BOX_PATH + " " : "";

        ShellUtils.CommandResult result = ShellUtils.execCommand(busyBoxPath + "cat /proc/mounts", false, true);
        if (!TextUtils.isEmpty(result.successMsg)) {
            return result.successMsg;
        }
        return "";
    }

    public static List<MountInfo> getMounts(String rawMountData) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(rawMountData));
            List<MountInfo> mounts = new ArrayList<>();

            try {
                String line;
                do {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        break;
                    }

                    String[] parts = line.split(" ");
                    if (parts.length < 6) {
                        continue;
                    }
                    MountInfo mount = new MountInfo();
                    mount.device = parts[0];
                    mount.mountpoint = parts[1];
                    mount.fs = parts[2];
                    mount.options = parts[3];
                    mount.fs_freq = Integer.parseInt(parts[4]);
                    mount.fs_passno = Integer.parseInt(parts[5]);
                    mounts.add(mount);
                } while (true);

                return mounts;
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to open /proc/mounts to get mountpoint info");
        }
    }

    public static boolean remount(String oldMode, String newMode) {
        List<MountInfo> mounts;
        mounts = MountInfo.getMounts();
        if (mounts == null) {
            return false;
        }

        String busyBoxPath = ShellUtils.exists(ShellUtils.BUSY_BOX_PATH) ?
                ShellUtils.BUSY_BOX_PATH + " " : "";

        int i = 0;
        for (MountInfo mount : mounts) {
            if (!TextUtils.isEmpty(mount.options)
                    && mount.options.contains(oldMode)
                    && (mount.mountpoint.equals(File.separator) || mount.mountpoint.equals("/system"))) {
                String mountCmd = busyBoxPath + "mount -o " + newMode + ",remount -t "
                        + mount.fs + " " + mount.device + " " + mount.mountpoint;
                ShellUtils.execCommand(mountCmd, true, true);
                i += 1;
                if (i >= 2) {
                    break;
                }
            }
        }

        return true;
    }

    public static boolean getAccessState() {
        List<MountInfo> mounts = MountInfo.getMounts();
        return mounts != null && mounts.size() > 0
                && mounts.get(0) != null && !mounts.get(0).options.contains("ro");
    }

    public static List<MountInfo> getFuseInfo() {
        List<MountInfo> allMounts = getMounts();
        List<MountInfo> fuseMounts = new ArrayList<>();

        for (MountInfo mountInfo : allMounts) {
            if (mountInfo.device.equals("/dev/fuse")) {
                fuseMounts.add(mountInfo);
            }
        }

        return fuseMounts;
    }

}
