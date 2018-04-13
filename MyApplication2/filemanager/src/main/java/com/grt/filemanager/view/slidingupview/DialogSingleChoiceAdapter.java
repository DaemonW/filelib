package com.grt.filemanager.view.slidingupview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.grt.filemanager.R;


public class DialogSingleChoiceAdapter extends BaseAdapter {

    private Context context;
    private CharSequence[] items;
    private int mCheckedItem;

    public DialogSingleChoiceAdapter(Context context, CharSequence[] items, int mCheckedItem) {
        this.context = context;
        this.items = items;
        this.mCheckedItem = mCheckedItem;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCheckedItem(int mCheckedItem) {
        this.mCheckedItem = mCheckedItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.dialog_sigle_choice_item, null);
        }

        TextView tv_item = (TextView) convertView.findViewById(R.id.tv_item);
        tv_item.setText(items[position]);

        RadioButton radioButton = (RadioButton) convertView
                .findViewById(R.id.radio_item);

        if (position == mCheckedItem) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }
        return convertView;
    }

}
