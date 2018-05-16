package com.daemonw.file;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.file.ui.dialog.FileChooseDialog;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = findViewById(R.id.tv_content);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            //PermissionUtil.requestPermission(MainActivity.this, Volume.MOUNT_USB);
            FileChooseDialog dialog = new FileChooseDialog(this);
            dialog.show();
        });
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void init() {
        if (!isGrantExternalRW(this)) {
            return;
        }
//        Intent intent = new Intent(MainActivity.this, FileManagerActivity.class);
//        MainActivity.this.startActivity(intent);
        Volume v = StorageUtil.getMountVolume(this, Volume.MOUNT_EXTERNAL);
        if (v == null) {
            return;
        }
        String filePath = v.mPath + "/encrypt.flv";
        Uri uri = pathToMediaUri(v.mPath, filePath);
        textView.append(uri.toString() + "\n");

    }


    private Uri pathToMediaUri(String volumePath, String path) {
        Uri mediaUri = MediaStore.Files.getContentUri(volumePath);

        Cursor cursor = getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "=" + path.substring(path.lastIndexOf("/") + 1),
                null,
                null);
        cursor.moveToFirst();

        Uri uri = ContentUris.withAppendedId(mediaUri, cursor.getLong(0));
        return uri;
    }

    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }
}
