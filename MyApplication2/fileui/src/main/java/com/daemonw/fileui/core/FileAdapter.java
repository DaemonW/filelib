package com.daemonw.fileui.core;

import android.content.Context;

import com.daemonw.filelib.model.Filer;
import com.daemonw.fileui.widget.adapter.CommonAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;
import com.example.fileui.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileAdapter extends CommonAdapter<Filer> {
    private static SimpleDateFormat formater=new SimpleDateFormat("yyyyMMdd hh:mm:ss", Locale.getDefault());

    public FileAdapter(Context context, int layoutResId, List<Filer> fileList) {
        super(context, layoutResId, fileList);
    }

    @Override
    protected void convert(ViewHolder holder, Filer file, int position) {
        holder.setText(R.id.file_name, file.getName());
        holder.setText(R.id.file_modified, formater.format(new Date(file.lastModified())));
    }

    public void update(List<Filer> filerList){
        mDatas.clear();
        mDatas.addAll(filerList);
        notifyDataSetChanged();
    }
}
