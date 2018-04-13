package com.grt.filemanager.view;

import android.content.Context;
import android.text.TextUtils;

import com.grt.filemanager.R;
import com.grt.filemanager.util.FileUtils;

/**
 * Created by LDC on 2018/3/14.
 */

public class ViewHelper {

    public static boolean checkNameRules(Context context, String name, EditTextCallBack callBack) {
        if (TextUtils.isEmpty(name)) {
            callBack.setError(context.getString(R.string.empty_name_tip));
            return false;
        } else if (FileUtils.hasIllegalChar(name)) {
            callBack.setError(context.getString(R.string.file_name_illegal));
            return false;
        } else if (FileUtils.fileNameOnlyOne(name)) {
            callBack.setError(context.getString(R.string.file_name_unqualified));
            return false;
        }

        return true;
    }
}
