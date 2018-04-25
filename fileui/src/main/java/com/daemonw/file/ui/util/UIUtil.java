package com.daemonw.file.ui.util;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.daemonw.file.R;


public class UIUtil {
    private static PopupWindow mPopupLoading;

    public static PopupWindow getPopupLoading(Activity context) {
        PopupWindow popUp = new PopupWindow();
        popUp.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popUp.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置不能取消
        popUp.setOutsideTouchable(false);
        popUp.setFocusable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.loading, null);
        popUp.setContentView(view);
        return popUp;
    }

    public static void showLoading(Activity context) {
        if (mPopupLoading == null) {
            mPopupLoading = getPopupLoading(context);
        }
        mPopupLoading.showAtLocation(context.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

    public static void cancelLoading() {
        if (mPopupLoading != null && mPopupLoading.isShowing()) {
            mPopupLoading.dismiss();
        }
    }
}
