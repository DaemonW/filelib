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
import android.widget.Toast;

import com.daemonw.file.core.model.Filer;
import com.daemonw.file.ui.FileChooseDialog;
import com.daemonw.file.ui.OnFileChooseListener;

import java.util.List;

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
            dialog.setOnFileSelectListener(new OnFileChooseListener() {
                @Override
                public void onFileSelect(List<Filer> selected) {
                    if (selected == null || selected.size() == 0) {
                        return;
                    }
                    Toast.makeText(MainActivity.this, "select: " +selected.get(0).getPath(),Toast.LENGTH_SHORT).show();
                }
            });
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
        Intent intent = new Intent(this, FileManagerActivity.class);
        startActivity(intent);
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
