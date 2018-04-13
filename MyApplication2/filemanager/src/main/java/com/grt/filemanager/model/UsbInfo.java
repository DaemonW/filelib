package com.grt.filemanager.model;

public class UsbInfo {

    private static String path;
    private static boolean register = false;

    public static final String USB_ACTION = "usb_action";
    public static final int USB_ACTION_ATTACHED = 1;
    public static final int USB_ACTION_MOUNTED = 2;

    public static final String USB_INFO = "usb_info";

    public static String getUsbPath() {
        return path;
    }

    public static void setUsbPath(String path) {
        UsbInfo.path = path;
    }

    public static boolean isRegister() {
        return register;
    }

    public static void setRegister(boolean register) {
        UsbInfo.register = register;
    }
}
