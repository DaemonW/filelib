package com.grt.daemonw.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.grt.daemonw.filelibrary.Constant;
import com.grt.daemonw.filelibrary.file.Filer;
import com.grt.daemonw.filelibrary.file.HybirdFile;
import com.grt.daemonw.filelibrary.reflect.Volume;
import com.grt.daemonw.filelibrary.utils.StorageUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mList;
    private Handler mHandler = new Handler();
    private static final int REQUEST_EXT_STORAGE_WRITE_PERM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mList = findViewById(R.id.list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener((v) -> {
            StorageUtil.requestPermission(MainActivity.this, Volume.MOUNT_USB);
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void init() {
        List<Volume> volumeList = StorageUtil.getVolumes(this);
        Volume otg = null;
        for (Volume v : volumeList) {
            if (v.mountType == Volume.MOUNT_USB) {
                otg = v;
                break;
            }
        }
        ArrayList<String> subFiles = new ArrayList<>();
        if (otg != null) {
            if (!StorageUtil.hasExtSdcardPermission(this, Volume.MOUNT_USB)) {
                StorageUtil.requestPermission(this, Volume.MOUNT_USB);
                return;
            }
            String otg_uri = PreferenceManager.getDefaultSharedPreferences(this).getString(Constant.PREF_USB_URI, null);
            Logger.d("otg uri =" + otg_uri);
            HybirdFile file = new HybirdFile(this, otg_uri);
            List<Filer> sub = file.listFiles();
            Logger.d("sub file size =" + sub.size());
            for (Filer f : sub) {
                subFiles.add(f.getName());
            }
        }
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subFiles);
        mList.setAdapter(adapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        StorageUtil.handlePermissionRequest(this, requestCode, resultCode, resultData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXT_STORAGE_WRITE_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
