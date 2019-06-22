package com.daemonw.file.ui;

import android.app.Activity;
import android.content.Intent;

public class FileChooseDialog {
    public static final int REQUEST_CHOOSE_FILE = 9041;
    public static final int TYPE_RAW = 0;
    public static final int TYPE_ACTIVITY = 1;

    private Activity mContext;
    private int type;
    private OnFileChooseListener onFileChooseListener;
    private boolean showFile = true;

    public FileChooseDialog(Activity context) {
        mContext = context;
        type = TYPE_RAW;
        this.showFile = true;
    }

    public FileChooseDialog(Activity context, boolean showFile) {
        mContext = context;
        type = TYPE_RAW;
        this.showFile = showFile;
    }

    private FileChooseDialog(Activity context, int type, boolean showFile) {
        mContext = context;
        this.type = type;
        this.showFile = showFile;
    }


    public void show() {
        switch (type) {
            case TYPE_RAW:
                FileChooseDialogInternal dialog = new FileChooseDialogInternal(mContext, showFile);
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
