package com.grt.daemonw.filelibyary.exception;

public class PermException extends Error {
    public int permCode;

    public static final int PERM_EXT = 0;
    public static final int PERM_USB = 1;

    public PermException(int perm) {
        super("permission denied");
        permCode = perm;
    }
}
