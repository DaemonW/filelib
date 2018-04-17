package com.grt.daemonw.filelibrary.receiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.orhanobut.logger.Logger;

import java.io.IOException;

/**
 * Created by daemonw on 4/14/18.
 */

public class UsbMountReceiver extends BroadcastReceiver {
    private static final String TAG = UsbMountReceiver.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.github.mjdev.libaums.USB_PERMISSION";
    UsbMassStorageDevice[] massStorageDevices;
    int currentDevice;
    FileSystem currentFs;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (ACTION_USB_PERMISSION.equals(action)) {

            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                if (device != null) {
                    setupDevice();
                }
            }

        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            // determine if connected device is a mass storage devuce
            if (device != null) {
                discoverDevice(context, intent);
            }

            Log.d(TAG, "USB device attached");
            Toast.makeText(context, "USB device attached", Toast.LENGTH_SHORT).show();


        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            Log.d(TAG, "USB device detached");
            // determine if connected device is a mass storage device
            if (device != null) {
                // check if there are other devices or set action bar title
                // to no device if not
                discoverDevice(context, intent);
            }
        }

    }

    private void setupDevice() {
        try {
            massStorageDevices[currentDevice].init();

            // we always use the first partition of the device
            currentFs = massStorageDevices[currentDevice].getPartitions().get(0).getFileSystem();
            Log.d(TAG, "Capacity: " + currentFs.getCapacity());
            Log.d(TAG, "Occupied Space: " + currentFs.getOccupiedSpace());
            Log.d(TAG, "Free Space: " + currentFs.getFreeSpace());
            Log.d(TAG, "Chunk size: " + currentFs.getChunkSize());
            UsbFile root = currentFs.getRootDirectory();
            Logger.e("device name = " + root.getName());
        } catch (IOException e) {
            Log.e(TAG, "error setting up device", e);
        }

    }

    private void discoverDevice(Context context, Intent intent) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return;
        }
        massStorageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        if (massStorageDevices.length == 0) {
            Log.w(TAG, "no device found!");
            return;
        }

        currentDevice = 0;
        UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (usbDevice == null) {
            return;
        }

        if (usbManager.hasPermission(usbDevice)) {
            Log.d(TAG, "received usb device via intent");
            // requesting permission is not needed in this case
            setupDevice();
        } else {
            // first request permission from user to communicate with the
            // underlying
            // UsbDevice
            PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                    ACTION_USB_PERMISSION), 0);
            usbManager.requestPermission(massStorageDevices[currentDevice].getUsbDevice(), permissionIntent);
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
