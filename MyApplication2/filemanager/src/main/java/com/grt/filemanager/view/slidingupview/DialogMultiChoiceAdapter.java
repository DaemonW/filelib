package com.grt.filemanager.view.slidingupview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grt.filemanager.R;


public class DialogMultiChoiceAdapter extends BaseAdapter {

    private Context context;
    private CharSequence[] items;
    private boolean[] mCheckedItems;

    public DialogMultiChoiceAdapter(Context context, CharSequence[] items, boolean[] mCheckedItems) {
        this.context = context;
        this.items = items;
        this.mCheckedItems = mCheckedItems;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.dialog_multi_choice_item, null);
        }

        TextView tv_item = (TextView) convertView.findViewById(R.id.tv_item);
        tv_item.setText(items[position]);
        CheckBox radioButton = (CheckBox) convertView.findViewById(R.id.checkbox_item);
        boolean b = mCheckedItems[position];
        radioButton.setChecked(b);

        return convertView;
    }

}
