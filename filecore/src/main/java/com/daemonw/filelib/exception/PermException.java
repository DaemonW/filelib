package com.daemonw.filelib.exception;

import java.io.IOException;

public class PermException extends IOException {
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
