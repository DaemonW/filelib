package com.daemonw.file.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.daemonw.file.FileConst;
import com.orhanobut.logger.Logger;

/**
 * Created by daemonw on 4/14/18.
 */

public class UsbMountReceiver extends BroadcastReceiver {
    private static final String TAG = UsbMountReceiver.class.getSimpleName();

    private final static String USB_STATE_ACTION = "android.hardware.usb.action.USB_STATE";
    private static final String USB_CONNECTED = "connected";
    private static final String USB_HOST_CONNECTED = "host_connected";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            // determine if connected device is a mass storage device
            if (device != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean(FileConst.PREF_USB_MOUNTED, true);
                editor.apply();
                Logger.d("USB Attached, Name = " + device.getProductName());
            }

        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            // determine if connected device is a mass storage device
            if (device != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean(FileConst.PREF_USB_MOUNTED, false);
                editor.apply();
                Logger.d("USB Detached, Name = " + device.getProductName());
            }
        } else if (USB_STATE_ACTION.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                boolean connected = intent.getExtras().getBoolean(USB_HOST_CONNECTED, false);
                Logger.d("USB Host State, connect = " + connected);
            }
        }

    }
}
