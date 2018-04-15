package com.grt.daemonw.filelibyary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by daemonw on 4/14/18.
 */

public class SdcardMountReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        switch (action){
            case Intent.ACTION_MEDIA_UNMOUNTED:
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                break;
            case Intent.ACTION_MEDIA_EJECT:
                break;
        }
    }
}
