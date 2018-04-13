package com.grt.filemanager.view.slidingupview;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.grt.filemanager.R;
import com.grt.filemanager.view.dialog.FeDialog;

import java.util.Stack;

/**
 * Created by LDC on 2018/3/15.
 */

public class BottomViewMgr {
    private static BottomViewMgr bottomViewMgr;

    private Stack<BottomView> mBottomViewStack;

    public static BottomViewMgr getMgr() {
        if (bottomViewMgr == null) {
            bottomViewMgr = new BottomViewMgr();
        }
        return bottomViewMgr;
    }

    private BottomViewMgr() {
        mBottomViewStack = new Stack<>();
    }

    public static void showBottomView(Context context, View insideView) {
        BottomView bottomView = new BottomView(context, R.style.BottomViewTheme_Defalut,
                insideView);
        bottomView.setAnimation(R.style.BottomToTopAnim);
        bottomView.initBottomView(false);
        bottomView.showBottomView();
        getMgr().setBottomView(bottomView);
    }

    public void setBottomView(BottomView bottomView) {
        if (mBottomViewStack == null) {
            mBottomViewStack = new Stack<>();
        }

        mBottomViewStack.add(bottomView);
    }

    public static void showSoftInput(final EditText editText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                InputMethodManager inputManager =
                        (InputMethodManager) editText.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    public static void hideBottomAndSoftInput(Context context, EditText fileName) {
        hideSoftwareInput(context, fileName);
        hideBottomView();
    }

    /**
     * 隐藏软键盘
     *
     * @param context  Context
     * @param editText 编辑框控件
     */
    public static void hideSoftwareInput(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void hideBottomView() {
        BottomView bottomView = getMgr().popStack();
        if (bottomView != null) {
            bottomView.dismissBottomView();
        }
    }

    private BottomView popStack() {
        return mBottomViewStack.size() > 0 ? mBottomViewStack.pop() : null;
    }

    public static void showBottomView(FeDialog feDialog) {
        feDialog.show();
    }
}
