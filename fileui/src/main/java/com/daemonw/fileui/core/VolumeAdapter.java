package com.daemonw.fileui.core;

import android.app.Activity;

import com.daemonw.filelib.reflect.Volume;
import com.daemonw.fileui.R;
import com.daemonw.fileui.widget.adapter.CommonAdapter;
import com.daemonw.fileui.widget.adapter.ViewHolder;

import java.util.List;

public class VolumeAdapter extends CommonAdapter<Volume> {

    public VolumeAdapter(Activity context, int layoutResId, List<Volume> volumes) {
        super(context, layoutResId, volumes);
    }

    @Override
    protected void convert(ViewHolder holder, Volume volume, int position) {
        holder.setText(R.id.volume_name, volume.mDescription);
        switch (volume.mountType) {
            case Volume.MOUNT_INTERNAL:
                holder.setImageResource(R.id.volume_icon,R.drawable.ic_internal_storage);
                break;
            case Volume.MOUNT_EXTERNAL:
                holder.setImageResource(R.id.volume_icon,R.drawable.ic_external_storage);
                break;
            case Volume.MOUNT_USB:
                holder.setImageResource(R.id.volume_icon,R.drawable.ic_usb_storage);
                break;
        }
    }
}
