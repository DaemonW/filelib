package com.grt.filemanager.apientrance;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.grt.filemanager.ViewConstant;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.model.ArchiveModel;
import com.grt.filemanager.model.DataFactory;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.RefrushData;
import com.grt.filemanager.model.SearchModel;
import com.grt.filemanager.model.imp.local.archive.ArchiveFactory;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.PermissionUtils;
import com.grt.filemanager.view.OperationViewHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by LDC on 2018/3/15.
 * 目前该库适用于本地文件数据、TF、OTG数据,其他外接设备有待测试
 * 目前提供文件读写数据、浏览文件、删除文件，创建文件、黏贴
 */

public class ApiPresenter {
    private static ApiPresenter apiPresenter;
    private static Activity activity;
    private static Context context;

    /**
     * Application 里面初始化context
     * @param context
     */
    public void initContext(Context context){
        this.context = context;
    }

    public void initActivity(Activity activity){
        this.activity = activity;
    }

    public static Context getContext() {
        return context;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static ApiPresenter getInstance(){
        if (apiPresenter == null){
            apiPresenter = new ApiPresenter();
        }
        return apiPresenter;
    }

    /**
     * 数据获取接口
     * @param args DATA_ID: 要查找的数据源ID,目前b本地数据与OTG为 DataConstant.STORAGE_DATA_ID
     *             ACCOUNT_ID:数据类型分类，本地默认为0,OTG等其他类型具体获取数据时分类
     *             FRAGMENT_ID：当前展示数据对应的Fragment的ID,用于分类存储于内存，避免重复获取数据
     *             TYPE:要执行操作分类
     *             PATH:当前文件路径,目前功能使用时为null即可
     *             SORT_TYPE：数据排序类型，目前都是按照标题类型
     *             SORT_ORDER:升降序分类
     *              ONLY_FOLDER:是否仅筛选文件夹
     *              SHOW_HIDE_FILE：是否显示隐藏文件
     *             注意: Data_Id, AccountId, type,三个为必传参数，其他用于扩展
     *
     * @return
     */
    public DataModel createData(Bundle args){
        int mDataId = args.getInt(ViewConstant.DATA_ID);
        int mAccountId = args.getInt(ViewConstant.ACCOUNT_ID);
        long mFragmentId = args.getLong(ViewConstant.FRAGMENT_ID);
        int mType = args.getInt(ViewConstant.TYPE);
        String mPath = args.getString(ViewConstant.PATH);
        int mSortType = args.getInt(ViewConstant.SORT_TYPE);
        int mSortOrder = args.getInt(ViewConstant.SORT_ORDER);
        boolean mOnlyFolder = args.getBoolean(ViewConstant.ONLY_FOLDER);
        boolean mShowHideFile = args.getBoolean(ViewConstant.SHOW_HIDE_FILE);

        int mPosition = args.getInt(ViewConstant.POSITION);
        final DataModel dataModel = DataFactory.createData(mDataId, mAccountId, mFragmentId);
        try {
            dataModel.setHideFileArg(mShowHideFile);
            switch (mType) {
                case ViewConstant.INIT:
                    dataModel.setCurrentPath(DataConstant.INIT_PATH,
                            dataModel.getRootPath(), mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.INIT_WITH_PATH:
                    dataModel.setRootPath(mPath);
                    dataModel.setCurrentPath(DataConstant.INIT_PATH,
                            dataModel.getRootPath(), mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.RESTORE:
                    dataModel.setPathStack(args.getString(ViewConstant.STACK));//TODO delete
                    dataModel.setCurrentPath(DataConstant.RESTORE_PATH,
                            mPath, mPosition, mSortType, mSortOrder);
                    if (!TextUtils.isEmpty(args.getString(ViewConstant.CHILD_PATH, ""))) {
                        dataModel.getPathStack().setPeekPosition(calcChildPosition(dataModel, args.getString(ViewConstant.CHILD_PATH, "")));
                    }
                    break;

                case ViewConstant.KEEP:
                    dataModel.setCurrentPath(DataConstant.KEEP_PATH,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.GO:
                    FileInfo current = null;
                    try {
                        current = dataModel.getCurrentChildren(args.getInt(ViewConstant.CLICK_POSITION));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                    if (current == null) {
                        break;
                    }
                    mPath = current.getPath();
                    if (current.isFolder() || !TextUtils.isEmpty(current.getSpecialInfo(DataConstant.LINK)
                            .getString(DataConstant.LINK))) {
                        dataModel.setCurrentFileInfo(current);
                        dataModel.setCurrentPath(DataConstant.GO_PATH, mPath, mPosition, mSortType, mSortOrder);
                    }
                    break;

                case ViewConstant.BACK:
                    dataModel.setCurrentPath(DataConstant.BACK_PATH,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.FASTBACK:
                    dataModel.setCurrentPath(DataConstant.FASTBACK_PATH,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.REFRESH:
                    dataModel.setCurrentPath(DataConstant.REFRESH,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.PREVIEW_ARCHIVE:
                    if (dataModel instanceof SearchModel) {
                        current = ArchiveFactory.getArchiveFile(mPath, args.getString(ViewConstant.PASSWORD));
                    } else {
                        ArchiveModel archiveModel = ((ArchiveModel) dataModel);
                        current = archiveModel.getFileInfo(mPath, args.getString(ViewConstant.PASSWORD), true);
                    }
                    dataModel.setCurrentFileInfo(current);
                    dataModel.setCurrentPath(DataConstant.GO_PATH, mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.PREVIEW_ARCHIVE_WITH_PATH:
                    dataModel.setRootPath(mPath);
                    dataModel.setCurrentPath(DataConstant.INIT_PATH,
                            dataModel.getRootPath(), mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.SEARCH:
                    SearchModel searchModel = ((SearchModel) dataModel);
                    searchModel.doSearch(mSortType, mSortOrder,
                            args.getInt(ViewConstant.SEARCH_DATA_ID),
                            args.getString(ViewConstant.SEARCH_KEY_WORD),
                            args.getString(ViewConstant.SEARCH_CUR_PATH), String.valueOf(args.getLong(ViewConstant.SEARCH_FRAGMENT_ID)));
                    break;

                case ViewConstant.SORT:
                    dataModel.setCurrentPath(DataConstant.SORT,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.FASTFORWARD:
                    dataModel.setCurrentPath(DataConstant.FAST_FORWARD_PATH,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                case ViewConstant.REINIT:
                    dataModel.setCurrentPath(DataConstant.REINIT,
                            mPath, mPosition, mSortType, mSortOrder);
                    break;

                default:
                    break;
            }

            if (mOnlyFolder) {
                dataModel.filtrationChildren(DataConstant.FOLDER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataModel;
    }

    private int calcChildPosition(DataModel dataModel, String mChildPath) {
        List<FileInfo> children = dataModel.getChildren();
        FileInfo file;
        int count = children.size();
        for (int index = 0; index < count; index++) {
            file = children.get(index);
            if (file.getPath().equals(mChildPath)) {
                return index * ViewConstant.POSITION_ARG;
            }
        }

        return 0;
    }

    /**
     * 创建文件或者文件夹
     * @param dataModel
     * @param path 文件或者文件夹的路径
     * @param parentPath 创建的文件夹或文件的父路径
     *  @param isFolder 是否是文件夹
     * @return
     */
    public void createFileOrFolder(final Activity context, final boolean isFolder, DataModel dataModel, int dataId, long fragmentId){
//        FileInfo fileInfo = dataModel.getFileInfo(path);
//        int result = fileInfo.create(isFolder);
//        if (result == ResultConstant.SUCCESS){
//            dataModel.addDbCache(fileInfo, null, true);
//
//            if (dataModel instanceof LocalModel) {
//                ((LocalModel) dataModel).updateAllParent(parentPath, 0, 1);
//            }
//        }
//        return new CreateInfo(fileInfo, fileInfo.create(isFolder));
        if (BuildUtils.thanMarshmallow()) {
            boolean hasPermission = PermissionUtils.checkHasPermission(context,
                    PermissionUtils.WRITE_EXTERNAL_STORAGE_PER[0]);
            if (!hasPermission) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "您没有权限", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }
        OperationViewHelper.createNewFileDialog(context, isFolder, dataModel, dataId, fragmentId);
    }

    /**
     * 删除文件或文件夹
     * @param file 待删除的文件或文件夹
     * @param dataId 数据源ID
     * @param desData 数据源数据
     * @return 删除是否成功
     */
    public Integer deleteFileOrFolder(FileInfo file, int dataId, DataModel desData){
//        LogToFile.e("APIPresenter", "APIPresenter file isFolder = " +file.isFolder());
        if (file.isFolder()) {
            return FileWork.deleteFolder(file, dataId, desData) ? ResultConstant.SUCCESS : ResultConstant.FAILED;
        } else {
            return FileWork.deleteFile(file, dataId, desData) ? ResultConstant.SUCCESS : ResultConstant.FAILED;
        }
    }

    /**
     *
     * @param srcData 原数据接口
     * @param desData 当前黏贴的目标路径数据接口
     * @param srcDataId 原数据ID类型
     * @param desDataId  目标数据ID类型
     * @param selectedList 待黏贴的文件列表
     */
    public void paste(final DataModel srcData, final DataModel desData, final int srcDataId, final int desDataId, final List<FileInfo> selectedList){
        if (!FileWork.checkOperationPermission(desData)){
            return;
        }
        List<FileInfo> listChild = desData.getCurrentFolder().getList(true);
        final String desFolderPath = desData.getCurrentFolder().getPath();
        int result = FileWork.checkConflictName(desDataId, srcDataId, selectedList, desFolderPath, listChild);
        if (result == ResultConstant.SUCCESS){
            Observable.from(selectedList)
                    .map(new Func1<FileInfo, Integer>() {
                        @Override
                        public Integer call(FileInfo fileInfo) {
                            Bundle args = FileWork.setArgs(fileInfo.getPath(), desFolderPath, true, false, srcDataId, desDataId);
                            return FileWork.copyWork(args, desData, srcData);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {
                            RefrushData data = new RefrushData();
                            data.setType(ViewConstant.PASTE);
                            data.setResult(ResultConstant.SUCCESS);
                            EventBus.getDefault().post(data);
                            unsubscribe();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            RefrushData data = new RefrushData();
                            data.setType(ViewConstant.PASTE);
                            data.setResult(ResultConstant.FAILED);
                            EventBus.getDefault().post(data);
                        }

                        @Override
                        public void onNext(Integer integer) {

                        }
                    });
        }else {
            RefrushData data = new RefrushData();
            data.setType(ViewConstant.PASTE);
            data.setResult(result);
            EventBus.getDefault().post(data);
        }
    }

    /**
     * 判定外接设备是否授权
     * @return
     */
    public Integer getUsbAuthorized(){
        String usbDevicePath = StorageManagerUtil.getInstant().getUsbDevicePathNew();
        if (usbDevicePath == null){
            return ViewConstant.NO_DEVICE;
        }else if (usbDevicePath.equals("")){
            return ViewConstant.NO_AUTHORIZE;
        }else {
            return ViewConstant.AUTHORIZED;
        }
    }

    /**
     * 授权外接设备
     * @param context
     */
    public void usbAuthorization(Activity context, boolean isSdcard){
        OperationViewHelper.createUsbAuthorizeDialog(context, isSdcard);
    }
}
