package com.daemonw.file;

/**
 * Created by daemonw on 4/14/18.
 */

public class FileConst {


    //shared preference key
    public static final String PREF_EXTERNAL_URI = "pref_external_uri";

    public static final String PREF_EXTERNAL_PATH = "pref_external_path";

    public static final String PREF_USB_URI = "pref_usb_uri";

    public static final String PREF_USB_MOUNTED = "pref_usb_mounted";


    //dumb file
    public static final String DUMB_FILE = ".dumb_";


    //Permission
    public static final int REQUEST_GRANT_EXTERNAL_PERMISSION = 0;
    public static final int REQUEST_GRANT_USB_PERMISSION = 1;


    //exception msg
    public static final String MSG_NO_PERM = "permission denied";
    public static final String MSG_NO_PERM_ON_EXTERNAL = "permission denied while accessing external storage";
    public static final String MSG_NO_PERM_ON_USB = "permission denied while accessing usb storage";
}
