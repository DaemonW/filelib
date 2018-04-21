package com.daemonw.filelib.exception;

public class PermException extends Exception {
    private int mountType;

    public static final int PERM_NO_ACCESS_ON_EXTERNAL = 0;
    public static final int PERM_NO_ACCESS_ON_USB = 1;

    public PermException(String msg, int mountType) {
        super(msg);
        this.mountType=mountType;
    }

    public int getMountType() {
        return mountType;
    }
}
