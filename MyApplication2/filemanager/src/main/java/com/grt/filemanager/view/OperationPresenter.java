package com.grt.filemanager.view;

import android.content.Context;

import com.grt.filemanager.ViewConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.DataFactory;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.LocalModel;
import com.grt.filemanager.model.RefrushData;
import com.grt.filemanager.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by LDC on 2018/3/14.
 */

public class OperationPresenter {
    private static OperationPresenter presenter;
    private Context mContext;
    private DataModel mCurData;
    private OperationPresenter(Context context, DataModel dataModel){
        this.mContext = context;
        this.mCurData = dataModel;
    }
    public static OperationPresenter getPresenter(Context context, DataModel dataModel) {
        if (presenter == null){
            presenter = new OperationPresenter(context, dataModel);
        }
        return presenter;
    }

    /**
     * 新建文件或文件夹
     *
     * @param newName  文件名称
     * @param isFolder 是否为文件夹
     * @param callBack 编辑框操作回调
     */
    public void create(final String newName, final boolean isFolder,
                       final EditTextCallBack callBack, int dataId, long fragmentId) {
        if (!ViewHelper.checkNameRules(mContext, newName, callBack)) {
            return;
        }

//        final MainFragmentView fragmentView = mView.getCurOperation();
//        int dataId = fragmentView.getDataId();
//        if (!mViewHelper.checkCloudNameRules(mContext, dataId, newName, callBack)) {
//            return;
//        }


        //判断文件是否重名
        if (!checkNameDuplicate(newName, fragmentId, callBack)) {
            return;
        }

        callBack.onFinish(newName);

        final boolean isNetId = DataConstant.isNetId(dataId);
//        if (isNetId) {
//            mViewHelper.showBottomLoading(R.string.doing_operation);
//        }

        final String parentPath = mCurData.getCurrentFolder().getPath();
        final String path = FileUtils.concatPath(parentPath) + newName;

//        FileManager.getInstance().createFileOrFolder(path, isFolder);
        createFileOrFolder(path, isFolder, dataId);
    }

    private boolean checkNameDuplicate(String newName, long fragmentId, EditTextCallBack callBack) {
        mCurData = DataFactory.getData(fragmentId);
        return checkFileName(mCurData, newName, callBack);
    }

    private boolean checkFileName(DataModel curData, String newName, EditTextCallBack callBack) {
        if (curData == null) {
            return true;
        }

        List<FileInfo> fileList = curData.getChildren();
        if (fileList != null) {
            for (FileInfo file : fileList) {
                if ((newName.toLowerCase()).equals(file.getName().toLowerCase())) {
                    callBack.setError(mContext.getString(com.grt.filemanager.R.string.same_name));
                    return false;
                }
            }
        }

        return true;
    }

    private class CreateInfo {
        public FileInfo file;
        public int result;

        public CreateInfo(FileInfo file, int result) {
            this.file = file;
            this.result = result;
        }
    }

    public void createFileOrFolder(final String path, final boolean isFolder, final int dataId) {
        Observable.create(new Observable.OnSubscribe<FileInfo>() {
            @Override
            public void call(Subscriber<? super FileInfo> subscriber) {
                subscriber.onNext(mCurData.getFileInfo(path));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<FileInfo, com.grt.filemanager.model.CreateInfo>() {
                    @Override
                    public com.grt.filemanager.model.CreateInfo call(FileInfo file) {
//                        LogToFile.e("asd", "FileManagerModel createFileOrFolder CreateInfo");
                        return new com.grt.filemanager.model.CreateInfo(file, file.create(isFolder));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<com.grt.filemanager.model.CreateInfo, com.grt.filemanager.model.CreateInfo>() {
                    @Override
                    public com.grt.filemanager.model.CreateInfo call(com.grt.filemanager.model.CreateInfo createInfo) {
                        RefrushData refrushData = new RefrushData();
                        refrushData.setData(mCurData);
                        refrushData.setDataId(dataId);
                        refrushData.setPath(path);
                        refrushData.setType(ViewConstant.CREATE_FILE_OR_FOLDER);
                        refrushData.setResult(createInfo.result);
                        EventBus.getDefault().post(refrushData);
                        return createInfo;
                    }
                })
                .filter(new Func1<com.grt.filemanager.model.CreateInfo, Boolean>() {
                    @Override
                    public Boolean call(com.grt.filemanager.model.CreateInfo createInfo) {
                        return createInfo.result == ResultConstant.SUCCESS;
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<com.grt.filemanager.model.CreateInfo>() {
                    @Override
                    public void call(com.grt.filemanager.model.CreateInfo createInfo) {
//                        updateAfterCreate(mCurData,
//                                createInfo, mCurData.getCurrentFolder().getPath());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void updateAfterCreate(DataModel dataModel, com.grt.filemanager.model.CreateInfo createInfo, String parentPath) {
        dataModel.addDbCache(createInfo.file, null, true);

        if (dataModel instanceof LocalModel) {
            ((LocalModel) dataModel).updateAllParent(parentPath, 0, 1);
        }
    }
}
