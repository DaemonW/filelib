package com.daemonw.filelib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daemonw.filelib.reflect.Volume;
import com.daemonw.filelib.utils.StorageUtil;
import com.daemonw.fileui.activity.FileChooseActivity;
import com.daemonw.fileui.dialog.FileChooseDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mVolumeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVolumeList = findViewById(R.id.volume_list);

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
        List<Volume> volumeList = StorageUtil.getVolumes(this);
        List<String> volumes = new ArrayList<>();
        for (Volume v : volumeList) {
            volumes.add(v.mDescription);
        }
        mVolumeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, volumes));
        mVolumeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Volume v = volumeList.get(position);
                Intent intent = new Intent(MainActivity.this, FileManagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("mount_point", v.mountType);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
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
