package com.grt.filemanager.adapter;

import android.os.Bundle;
import android.view.View;

import com.grt.filemanager.ViewConstant;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.SortConstant;
import com.grt.filemanager.fragment.DataFragment;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LogToFile;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by LDC on 2018/3/19.
 */

public class BaseItemListener implements View.OnClickListener, View.OnLongClickListener {
    protected int currentDataId;
    protected int currentAccountId;
    protected long mFragmentId;
    private AdapterViewRefrush view;
    protected static final StateHandler handler = StateHandler.getInstance();
    protected DataFragment fragment;
    public BaseItemListener(int dataId, int accountId, long mFragmentId, DataFragment fragment){
        currentAccountId = accountId;
        currentDataId = dataId;
        this.mFragmentId = mFragmentId;
        this.fragment = fragment;
    }

    public void setViewRefrush(AdapterViewRefrush view){
        this.view = view;
    }
    @Override
    public void onClick(View v) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) v.getTag();
        if (!handler.getIsCopyState()){
            if (handler.getSelectState()) {
                handleSelectState(baseViewHolder);
            } else {
                handlerClick(baseViewHolder);
            }
        }else {
            String path = baseViewHolder.getPath();
            if (handler.getSelectedParentPath().equals(FileUtils.getParentPath(path)) &&
                    handler.isSelected(path)) {
                return;
            }

            if (DataConstant.isPreviewArchive(baseViewHolder.getMimeType())) {
                return;
            }

            handlerClick(baseViewHolder);
        }
    }

    private void handlerClick(BaseViewHolder baseViewHolder){
        if (baseViewHolder.isFolder()){
            GoToClickPosition(baseViewHolder);
        }
    }

    protected void handleSelectState(BaseViewHolder baseViewHolder) {
        if (handler.getSelectedItemCount() > 0) {

            int curPosition = handler.selectItem(baseViewHolder.getPath(), baseViewHolder.getLayoutPosition());
            fragment.getDataAdapter().notifyItemChanged(curPosition);

            if (handler.getSelectedItemCount() <= 0) {
                handler.setSelectState(false);
                handler.setSelectedFragmentId(-1);
//                EventBusHelper.showDefaultToolbar(fragment.getDataAdapter().getDataId());
            } else {
//                EventBusHelper.showOperation(fragment.getDataAdapter().getOperationMenu(baseViewHolder.getFileInfo()),
//                        true, false, handler.getSelectedItemCount());
            }

        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (handler.getIsCopyState()){
            return true;
        }

        BaseViewHolder baseViewHolder = (BaseViewHolder) v.getTag();

        if (handler.getSelectState()) {
            int[] changePosRange = handler.longClickAgain(fragment.getDataAdapter(), baseViewHolder.getLayoutPosition());
            fragment.getDataAdapter().notifyItemRangeChanged(changePosRange[0], changePosRange[1]);

//            EventBusHelper.showOperation(fragment.getOperationMenu(baseViewHolder.getFileInfo()),
//                    true, false, handler.getSelectedItemCount());
        } else {

//            if (mFragment.isInArchive() && (mFragment.getDataId() == DataConstant.RECENT_OPEN_ID)) {
//                return true;
//            }

            String operationPermission = baseViewHolder.getFileInfo()
                    .getSpecialInfo(DataConstant.OPERATION_PERMISSION)
                    .getString(DataConstant.OPERATION_PERMISSION, DataConstant.ALL_ALLOW_PERMISSION);
            if (DataConstant.canLongSelected(operationPermission)) {
                handler.setSelectState(true);
                handler.setSelectedFragmentId(fragment.getmFragmentId());
                handler.setSelectedParentPath(FileUtils.getParentPath(baseViewHolder.getPath()));

                int curPosition = handler.selectItem(baseViewHolder.getPath(), baseViewHolder.getLayoutPosition());
                fragment.getDataAdapter().notifyItemChanged(curPosition);

//                EventBusHelper.showOperation(
//                        mFragment.getOperationMenu(baseViewHolder.getFileInfo()), true, true,
//                        handler.getSelectedItemCount());
            }
        }

        return true;
    }

    private void GoToClickPosition(final BaseViewHolder baseViewHolder){

        Observable.create(new Observable.OnSubscribe<DataModel>() {
            @Override
            public void call(Subscriber<? super DataModel> subscriber) {
                subscriber.onNext(ApiPresenter.getInstance().createData(setArgs(baseViewHolder)));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(DataModel data) {
                        if (view != null) view.viewRefrush(data, currentDataId);
                    }
                });
    }

    private Bundle setArgs(BaseViewHolder baseViewHolder){
        Bundle args = new Bundle();
        args.putInt(ViewConstant.ACCOUNT_ID, currentAccountId);
        args.putInt(ViewConstant.DATA_ID, currentDataId);
        args.putLong(ViewConstant.FRAGMENT_ID, mFragmentId);
        args.putInt(ViewConstant.TYPE, ViewConstant.GO);
        args.putInt(ViewConstant.CLICK_POSITION, baseViewHolder.getLayoutPosition());
        args.putInt(ViewConstant.SORT_TYPE, SortConstant.TITLE_ORDER);
        args.putInt(ViewConstant.SORT_ORDER,SortConstant.ASCENDING_ORDER);
        return args;
    }
}
