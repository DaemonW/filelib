package com.grt.daemonw.filelibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.preference.PreferenceManager;

import com.grt.daemonw.filelibrary.Constant;
import com.orhanobut.logger.Logger;

/**
 * Created by daemonw on 4/14/18.
 */

public class UsbMountReceiver extends BroadcastReceiver {
    private static final String TAG = UsbMountReceiver.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.github.mjdev.libaums.USB_PERMISSION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if (device != null) {
                    Logger.d("setup device");
                }
            }

        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            // determine if connected device is a mass storage device
            if (device != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(Constant.PREF_USB_DEVICE_NAME, device.getDeviceName());
                editor.putBoolean(Constant.PREF_USB_MOUNTED, true);
                editor.apply();
                Logger.d("USB Device Name = " + device.getProductName());
            }

        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            // determine if connected device is a mass storage device
            if (device != null) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString(Constant.PREF_USB_DEVICE_NAME, null);
                editor.putBoolean(Constant.PREF_USB_MOUNTED, false);
                editor.apply();
            }
        }

    }

    public static final String USB_CONNECTED = "connected";
    public static final String USB_HOST_CONNECTED = "host_connected";
    public static final String USB_CONFIGURED = "configured";
    public static final String USB_DATA_UNLOCKED = "unlocked";
    public static final String USB_CONFIG_CHANGED = "config_changed";
    public static final String EXTRA_DEVICE = "device";
    public static final String EXTRA_PERMISSION_GRANTED = "permission";

    public static final String ACTION_USB_STATE =
            "android.hardware.usb.action.USB_STATE";
}
