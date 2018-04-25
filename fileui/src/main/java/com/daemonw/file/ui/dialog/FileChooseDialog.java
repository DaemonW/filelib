package com.daemonw.file.ui.dialog;

import android.app.Activity;
import android.content.Intent;

import com.daemonw.file.ui.activity.FileChooseActivity;
import com.daemonw.file.ui.OnFileChooseListener;

public class FileChooseDialog {
    public static final int REQUEST_CHOOSE_FILE = 9041;
    public static final int TYPE_RAW = 0;
    public static final int TYPE_ACTIVITY = 1;

    private Activity mContext;
    private int type;
    private OnFileChooseListener onFileChooseListener;

    public FileChooseDialog(Activity context) {
        mContext = context;
        type = TYPE_RAW;
    }

    private FileChooseDialog(Activity context, int type) {
        mContext = context;
        this.type = type;
    }


    public void show() {
        switch (type) {
            case TYPE_RAW:
                FileChooseDialogInternal dialog = new FileChooseDialogInternal(mContext);
                dialog.setOnFileSelectListener(onFileChooseListener);
                dialog.show();
                break;
            case TYPE_ACTIVITY:
                Intent intent = new Intent(mContext, FileChooseActivity.class);
                mContext.startActivityForResult(intent, REQUEST_CHOOSE_FILE);
                break;
        }
    }

    public void setOnFileSelectListener(OnFileChooseListener onFileSelectListener) {
        this.onFileChooseListener = onFileSelectListener;
    }

}