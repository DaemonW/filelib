package com.daemonw.fileui.core;

import android.content.Context;

import com.daemonw.filelib.model.Filer;
import com.daemonw.filelib.model.LocalFile;
import com.daemonw.fileui.widget.adapter.CommonAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;
import com.example.fileui.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class FileAdapter extends CommonAdapter<Filer> {
    private int fileDepth = 0;
    private Filer mCurrent;
    private FileComparator mFileComparator = new FileComparator();

    private static SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd hh:mm:ss", Locale.getDefault());

    public FileAdapter(Context context, int layoutResId, String rootPath) {
        super(context, layoutResId, new ArrayList<Filer>());
        mCurrent = new LocalFile(context, rootPath);
        mDatas.addAll(sortFile(mCurrent.listFiles()));
    }

    @Override
    protected void convert(ViewHolder holder, Filer file, int position) {
        holder.setText(R.id.file_name, file.getName());
        holder.setText(R.id.file_modified, formater.format(new Date(file.lastModified())));
        if (file.isDirectory()) {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_folder_main);
        } else {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_file_fab);
        }
    }

    public void update(List<Filer> fileList) {
        mDatas.clear();
        mDatas.addAll(sortFile(fileList));
    }

    public void updateToParent() {
        if (isRoot()) {
            return;
        }
        mCurrent = mCurrent.getParentFile();
        mDatas.clear();
        mDatas.addAll(sortFile(mCurrent.listFiles()));
        fileDepth--;
    }

    public void updateToChild(Filer file) {
        mCurrent = file;
        mDatas.clear();
        mDatas.addAll(sortFile(mCurrent.listFiles()));
        fileDepth++;
    }

    public boolean isRoot() {
        return fileDepth <= 0;
    }

    public Filer getCurrent() {
        return mCurrent;
    }

    private List<Filer> sortFile(List<Filer> fileList) {
        Collections.sort(fileList, mFileComparator);
        return fileList;
    }

    class FileComparator implements Comparator<Filer> {
        @Override
        public int compare(Filer f1, Filer f2) {
            if (f1.isDirectory() == f2.isDirectory()) {
                return f1.getName().compareTo(f2.getName());
            }
            return f1.isDirectory() ? -1 : 1;
        }
    }
}
