package com.daemonw.file.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.PermissionUtil;
import com.daemonw.file.R;

public class GrantPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mountPoint = getIntent().getIntExtra("mount_point", Volume.MOUNT_UNKNOWN);
        if (mountPoint != Volume.MOUNT_UNKNOWN) {
            PermissionUtil.requestPermission(this, mountPoint);
        } else {
            finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode != FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION &&
                    requestCode != FileConst.REQUEST_GRANT_USB_PERMISSION) {
                return;
            }

            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }
            getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            if (requestCode == FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION) {
                sp.edit().putString(FileConst.PREF_EXTERNAL_URI, treeUri.toString()).apply();
            } else if (requestCode == FileConst.REQUEST_GRANT_USB_PERMISSION) {
                sp.edit().putString(FileConst.PREF_USB_URI, treeUri.toString()).apply();
            }
            finish();
        } else {
            finish();
            Toast.makeText(this, R.string.warn_grant_perm, Toast.LENGTH_SHORT).show();
        }
    }
}
