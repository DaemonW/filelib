package com.daemonw.file.ui;

import android.app.Activity;
import android.content.Context;

import com.daemonw.file.core.reflect.Volume;
import com.daemonw.file.core.utils.StorageUtil;
import com.daemonw.widget.CommonAdapter;
import com.daemonw.widget.ItemViewDelegate;
import com.daemonw.widget.ViewHolder;

import java.util.List;

public class VolumeAdapter extends CommonAdapter<Volume> {
    private Context context;

    public VolumeAdapter(Activity context, List<Volume> volumes) {
        super(context, volumes);
        this.context = context;
        addItemViewDelegate(new ItemViewDelegate<Volume>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.volume_item;
            }

            @Override
            public boolean isMatchedType(Volume item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, Volume t, int position) {
                VolumeAdapter.this.convert(holder, t, position);
            }
        });
    }

    @Override
    protected void convert(ViewHolder holder, Volume volume, int position) {
        holder.setText(R.id.volume_name, volume.mDescription);
        switch (volume.mountType) {
            case Volume.MOUNT_INTERNAL:
                holder.setImageResource(R.id.volume_icon, R.drawable.ic_internal_storage);
                break;
            case Volume.MOUNT_EXTERNAL:
                holder.setImageResource(R.id.volume_icon, R.drawable.ic_external_storage);
                break;
            case Volume.MOUNT_USB:
                holder.setImageResource(R.id.volume_icon, R.drawable.ic_usb_storage);
                break;
        }
    }

    public void refresh() {
        List<Volume> volumes = StorageUtil.getVolumes(context);
        mDatas.clear();
        mDatas.addAll(volumes);
        notifyDataSetChanged();
    }
}
