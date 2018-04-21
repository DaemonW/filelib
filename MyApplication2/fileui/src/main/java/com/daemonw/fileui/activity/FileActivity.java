package com.daemonw.fileui.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.daemonw.filelib.model.Filer;
import com.daemonw.fileui.core.FileAdapterWrapper;
import com.daemonw.fileui.widget.adapter.MultiItemTypeAdapter;
import com.example.fileui.R;

public class FileActivity extends AppCompatActivity implements MultiItemTypeAdapter.OnItemClickListener {

    private RecyclerView mFileListView;
    private FileAdapterWrapper mFileAdapterWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_activity);
        mFileListView = (RecyclerView) findViewById(R.id.file_list);
        mFileListView.setLayoutManager(new LinearLayoutManager(this));
        Bundle bundle = getIntent().getExtras();
        String rootPath = bundle != null ? bundle.getString("root_path") : null;
        if (rootPath == null) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        mFileAdapterWrapper = new FileAdapterWrapper(this, R.layout.file_item, rootPath);
        mFileAdapterWrapper.setOnItemClickListener(this);
        mFileListView.setAdapter(mFileAdapterWrapper);
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        Filer file = mFileAdapterWrapper.getItem(position);
        if (file == null) {
            return;
        }

        Toast.makeText(this,file.getPath(),Toast.LENGTH_SHORT).show();

        if (file.isDirectory()) {
            mFileAdapterWrapper.updateToChild(file);
        } else {

        }
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mFileAdapterWrapper != null && !mFileAdapterWrapper.isRoot()) {
            mFileAdapterWrapper.updateToParent();
        } else {
            super.onBackPressed();
        }
    }
}
