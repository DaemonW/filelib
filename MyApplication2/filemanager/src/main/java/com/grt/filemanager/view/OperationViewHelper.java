package com.grt.filemanager.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grt.filemanager.DocTreeHelper;
import com.grt.filemanager.R;
import com.grt.filemanager.ViewConstant;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.apientrance.FileWork;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.RefrushData;
import com.grt.filemanager.view.dialog.FeDialog;
import com.grt.filemanager.view.slidingupview.BottomViewMgr;
import com.grt.filemanager.view.slidingupview.SlidingUpDialog;
import com.grt.filemanager.view.slidingupview.SlidingUpDialogInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by LDC on 2018/3/14.
 */

public class OperationViewHelper {

    public static void createNewFileDialog(final Context context, final boolean isFolder, DataModel dataModel, int dataId, long fragmentId) {
        createNewFileDialog(context, isFolder, OperationPresenter.getPresenter(context,dataModel), dataId, fragmentId);
    }

    public static void createNewFileDialog(final Context context, final boolean isFolder,
                                           final OperationPresenter presenter, final int dataId, final long fragmentId) {
        View view = View.inflate(context, R.layout.copy2_creat_dir_and_rename_dlg_layout, null);

        final EditText fileName = (EditText) view.findViewById(R.id.sql_create_name);
        fileName.setText(isFolder ? R.string.create_dir : R.string.create_file);
        fileName.setSelection(0, fileName.getText().toString().length());

        SlidingUpDialog.Builder builder = new SlidingUpDialog.Builder(context);
        int titleResId = isFolder ? R.string.create_dir : R.string.create_file;

        builder.setTitle(titleResId)
                .setView(view)
                .setNeutralButton(R.string.cancel,
                        new SlidingUpDialogInterface.OnClickListener() {
                            @Override
                            public void onClick(SlidingUpDialogInterface dialog, int which) {
                                BottomViewMgr.hideBottomAndSoftInput(context, fileName);
                            }
                        })
                .setPositiveButton(R.string.okey,
                        new SlidingUpDialogInterface.OnClickListener() {
                            @Override
                            public void onClick(SlidingUpDialogInterface dialog, int which) {
                                presenter.create(fileName.getText().toString().trim(),
                                        isFolder, new EditTextCallBack() {
                                            @Override
                                            public void onFinish(String content) {
                                                BottomViewMgr.hideBottomAndSoftInput(context, fileName);
                                            }

                                            @Override
                                            public void setError(String errorMsg) {
                                                if (errorMsg != null) {
                                                    fileName.setError(errorMsg);
                                                }
                                            }
                                        }, dataId, fragmentId);
                            }
                        });
        SlidingUpDialog dialog = builder.create();
        dialog.hideTitleDivider();
        BottomViewMgr.showBottomView(context, dialog);
        BottomViewMgr.showSoftInput(fileName);
    }

    public static void createDeleteDialog(final Activity context, final int dataId,
                                          List<Integer> selectedItems,
                                          DataModel data) {
        boolean isNetId = DataConstant.isNetId(dataId);
        if (isNetId || dataId == DataConstant.RECYCLE_BIN_ID) {
//            createDeleteNoRecycleDialog(context, fragment, selectedCount, true);
        } else {
            createDeleteHasRecycleDialog(context, dataId, selectedItems, data);
        }
    }

    public static void createDeleteHasRecycleDialog(final Activity context, final int dataId,
                                                    final List<Integer> selectedItems, final DataModel data) {

        View view = View.inflate(context, R.layout.delete_dlg_layout_center, null);
        TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        String tip = context.getString(R.string.delete_hint_tip).replace("&", String.valueOf(selectedItems.size()));
        tv_tip.setText(tip);

        FeDialog.Builder builder = new FeDialog.Builder(context);
        builder.setView(view)
                .setNeutralButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BottomViewMgr.hideBottomView();
                            }
                        })
                .setPositiveButton(R.string.okey,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                BottomViewMgr.hideBottomView();

                                final ArrayList<FileInfo> selectedList = new ArrayList<>();
                                for (int position : selectedItems) {
                                    selectedList.add(data.getChildren().get(position));
                                }
                                Observable.from(selectedList)
                                        .map(new Func1<FileInfo, Integer>() {
                                            @Override
                                            public Integer call(FileInfo fileInfo) {
                                                return ApiPresenter.getInstance().deleteFileOrFolder(fileInfo, dataId, data);
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<Integer>() {
                                            @Override
                                            public void onCompleted() {
                                                RefrushData data = new RefrushData();
                                                data.setType(ViewConstant.DELETE_FILE_OR_FOLDER);
                                                data.setResult(ResultConstant.SUCCESS);
                                                EventBus.getDefault().post(data);
                                                unsubscribe();
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                RefrushData data = new RefrushData();
                                                data.setType(ViewConstant.DELETE_FILE_OR_FOLDER);
                                                data.setResult(ResultConstant.FAILED);
                                                EventBus.getDefault().post(data);
                                            }

                                            @Override
                                            public void onNext(Integer integer) {

                                            }
                                        });
                            }
                        });

        BottomViewMgr.showBottomView(builder.create());
    }

    public static void createUsbAuthorizeDialog(final Context context, final boolean isSdcard){
        View view = View.inflate(context, R.layout.delete_dlg_layout_center, null);
        TextView tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        String tip = context.getString(R.string.usb_authorize);
        tv_tip.setText(tip);

        FeDialog.Builder builder = new FeDialog.Builder(context);
        builder.setView(view)
                .setNeutralButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BottomViewMgr.hideBottomView();
                            }
                        })
                .setPositiveButton(R.string.okey,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BottomViewMgr.hideBottomView();

//                                String path = PreferenceUtils.getPrefString(context, SettingConstant.MEDIA_MOUNTED_PATH, "");
//                                UsbInfo.setUsbPath(path);
                                if (isSdcard){
//                                    Intent myIntent = new Intent(context, DataActivity.class);
//                                    myIntent.putExtra(ViewConstant.NEW_INTENT_ACTION, ViewConstant.ADD_EXTERNAL_SDCARD);
//                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(myIntent);
                                    DocTreeHelper.startOpenDocTreeIntent((Activity) context);
                                }else {
//                                    Intent myIntent = new Intent(context, DataActivity.class);
//                                    myIntent.putExtra(ViewConstant.NEW_INTENT_ACTION, ViewConstant.ADD_USB_MASS_STORAGE);
//                                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    myIntent.putExtra(UsbInfo.USB_ACTION, UsbInfo.USB_ACTION_MOUNTED);
////                                    myIntent.putExtra(UsbInfo.USB_INFO, path);
//                                    context.startActivity(myIntent);
                                    DocTreeHelper.startUsbGrantIntent((Activity) context);
                                }
                            }
                        });

        BottomViewMgr.showBottomView(builder.create());
    }
}
