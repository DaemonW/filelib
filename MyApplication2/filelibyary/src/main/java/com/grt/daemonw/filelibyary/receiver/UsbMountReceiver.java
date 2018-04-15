package com.grt.daemonw.filelibyary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

/**
 * Created by daemonw on 4/14/18.
 */

public class UsbMountReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("wangpeng", "action : " + action);

        if (intent.hasExtra(UsbManager.EXTRA_PERMISSION_GRANTED)) {
            boolean permissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
            Log.e("wangpeng", "permissionGranted : " + permissionGranted);
        }
        switch (action) {
            case UsbManager.ACTION_USB_ACCESSORY_ATTACHED:
            case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                //Name of extra for ACTION_USB_ACCESSORY_ATTACHED and ACTION_USB_ACCESSORY_DETACHED broadcasts containing the UsbAccessory object for the accessory.
                UsbAccessory accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
                Log.e("wangpeng", accessory.toString());
                break;
            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
                //Name of extra for ACTION_USB_DEVICE_ATTACHED and ACTION_USB_DEVICE_DETACHED broadcasts containing the UsbDevice object for the device.
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.e("wangpeng", device.toString());
                break;
            case ACTION_USB_STATE:
                    /*
                     * <li> {@link #USB_CONNECTED} boolean indicating whether USB is connected or disconnected.
                     * <li> {@link #USB_CONFIGURED} boolean indicating whether USB is configured.
                     * currently zero if not configured, one for configured.
                     * <li> {@link #USB_FUNCTION_ADB} boolean extra indicating whether the
                     * adb function is enabled
                     * <li> {@link #USB_FUNCTION_RNDIS} boolean extra indicating whether the
                     * RNDIS ethernet function is enabled
                     * <li> {@link #USB_FUNCTION_MTP} boolean extra indicating whether the
                     * MTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * PTP function is enabled
                     * <li> {@link #USB_FUNCTION_PTP} boolean extra indicating whether the
                     * accessory function is enabled
                     * <li> {@link #USB_FUNCTION_AUDIO_SOURCE} boolean extra indicating whether the
                     * audio source function is enabled
                     * <li> {@link #USB_FUNCTION_MIDI} boolean extra indicating whether the
                     * MIDI function is enabled
                     * </ul>
                     */
                boolean connected = intent.getBooleanExtra(USB_CONNECTED, false);
                Log.e("wangpeng", "connected : " + connected);
                boolean configured = intent.getBooleanExtra(USB_CONFIGURED, false);
                Log.e("wangpeng", "configured : " + configured);
                boolean function_adb = intent.getBooleanExtra(USB_FUNCTION_ADB, false);
                Log.e("wangpeng", "function_adb : " + function_adb);
                boolean function_mtp = intent.getBooleanExtra(USB_FUNCTION_MTP, false);
                Log.e("wangpeng", "function_mtp : " + function_mtp);
                boolean function_ptp = intent.getBooleanExtra(USB_FUNCTION_PTP, false);
                Log.e("wangpeng", "usb_function_ptp : " + function_ptp);
                break;
        }
    }

    public static final String USB_CONNECTED = "connected";
    public static final String USB_HOST_CONNECTED = "host_connected";
    public static final String USB_CONFIGURED = "configured";
    public static final String USB_DATA_UNLOCKED = "unlocked";
    public static final String USB_CONFIG_CHANGED = "config_changed";
    public static final String USB_FUNCTION_ADB = "adb";
    public static final String USB_FUNCTION_MTP = "mtp";
    public static final String USB_FUNCTION_PTP = "ptp";
    public static final String USB_FUNCTION_ACCESSORY = "accessory";
    public static final String EXTRA_PORT = "port";
    public static final String EXTRA_PORT_STATUS = "portStatus";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_ACCESSORY = "accessory";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";

    public static final String ACTION_USB_STATE =
            "android.hardware.usb.action.USB_STATE";
}
