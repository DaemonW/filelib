package com.daemonw.fileui.core;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.daemonw.filelib.model.Filer;
import com.daemonw.filelib.model.HybirdFile;
import com.daemonw.fileui.R;
import com.daemonw.fileui.widget.adapter.CommonAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

class FileAdapter extends CommonAdapter<Filer> {
    private int fileDepth = 0;
    private Filer mCurrent;
    private FileComparator mFileComparator = new FileComparator();
    private boolean mMultiSelect;
    private Set<Filer> mSelected = new HashSet<>();

    private static SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd hh:mm:ss", Locale.getDefault());

    public FileAdapter(Activity context, int layoutResId, String rootPath, int mountType) {
        super(context, layoutResId, new ArrayList<Filer>());
        mCurrent = new HybirdFile(context, rootPath, mountType);
        List<Filer> files = mCurrent.listFiles();
        mDatas.addAll(sortFile(files));
    }

    public void setMultiSelect(boolean enable) {
        mMultiSelect = enable;
    }

    public boolean isMultiSelect() {
        return mMultiSelect;
    }

    @Override
    protected void convert(ViewHolder holder, final Filer file, int position) {
        holder.setText(R.id.file_name, file.getName());
        holder.setText(R.id.file_modified, formater.format(new Date(file.lastModified())));
        if (file.isDirectory()) {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_folder);
        } else {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_file);
        }
        CheckBox checkBox = holder.getView(R.id.file_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                file.setChecked(isChecked);
                if (isChecked) {
                    mSelected.add(file);
                } else {
                    mSelected.remove(file);
                }
            }
        });
        if (mMultiSelect) {
            checkBox.setChecked(file.isChecked());
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        }
    }

    public void update(List<Filer> fileList) {
        mDatas.clear();
        mSelected.clear();
        mDatas.addAll(sortFile(fileList));
    }

    public void updateToParent() {
        if (isRoot()) {
            return;
        }
        mCurrent = mCurrent.getParentFile();
        mDatas.clear();
        mSelected.clear();
        mDatas.addAll(sortFile(mCurrent.listFiles()));
        fileDepth--;
    }

    public void updateToChild(Filer file) {
        mCurrent = file;
        mSelected.clear();
        mDatas.clear();
        mDatas.addAll(sortFile(mCurrent.listFiles()));
        fileDepth++;
    }

    public void updateCurrent() {
        mDatas.clear();
        mSelected.clear();
        mDatas.addAll(sortFile(mCurrent.listFiles()));
    }

    public boolean isRoot() {
        return fileDepth <= 0;
    }

    public Filer getCurrent() {
        return mCurrent;
    }

    public Set<Filer> getSelected() {
        return mSelected;
    }

    public void clearSelect() {
        for (Filer f : mSelected) {
            f.setChecked(false);
        }
        mSelected.clear();
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
