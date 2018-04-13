package com.grt.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grt.filemanager.DataActivity;
import com.grt.filemanager.R;
import com.grt.filemanager.ViewConstant;
import com.grt.filemanager.adapter.AdapterViewRefrush;
import com.grt.filemanager.adapter.BaseItemListener;
import com.grt.filemanager.adapter.DataAdapter;
import com.grt.filemanager.adapter.ItemListenerFactory;
import com.grt.filemanager.adapter.StateHandler;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.SortConstant;
import com.grt.filemanager.model.DataAccountInfo;
import com.grt.filemanager.model.DataAccountManger;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.util.TimeUtils;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by LDC on 2018/3/19.
 */

public class DataFragment extends Fragment implements AdapterViewRefrush, SwipeRefreshLayout.OnRefreshListener{
    private String TAG = "DataFragment";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataAdapter dataAdapter;
    private DataModel mData;
    private BaseItemListener itemListener;
    private int mDataId;
    private long mFragmentId;
    private Bundle args;
    private int mAccount;
    private static StateHandler stateHandler = StateHandler.getInstance();
    private View emptyLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentId = TimeUtils.getCurrentTime();
        args = getArguments();
        if (args != null){
            args.putLong(ViewConstant.FRAGMENT_ID, mFragmentId);
//            String dataType = args.getString(ViewConstant.DATA_TYPE);
//            if (dataType != null && dataType.equals(ViewConstant.OTG)){
//                setmAccountAndDataId();
//            }else {
            mAccount = args.getInt(ViewConstant.ACCOUNT_ID);
            mDataId = args.getInt(ViewConstant.DATA_ID);
//            }
            itemListener = ItemListenerFactory.createItemListener(mDataId, mAccount, mFragmentId, this);
            itemListener.setViewRefrush(this);
        }
    }

    private void setmAccountAndDataId(){
        List<DataAccountInfo> accounts = DataAccountManger.getAccountInfo(DataConstant.STORAGE_DATA_ID).getAccountList();
        int accountName;
        int accountId;
        for (DataAccountInfo account : accounts) {
            accountName = account.getRootName();
            accountId = account.getAccountId();

            if (accountName == R.string.mount_usb && accountId > 0) {
//                LogToFile.e(TAG, TAG + "setmAccountAndDataId account = "+accountId);
                mDataId = account.getDataId();
                args.putInt(ViewConstant.ACCOUNT_ID, accountId);
                mAccount = accountId;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.data_fragment, null);
        emptyLayout = layout.findViewById(R.id.empty_layout);
        if (recyclerView == null) recyclerView = (RecyclerView) layout.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (dataAdapter == null) dataAdapter = new DataAdapter(getContext(), itemListener, mData);
        recyclerView.setAdapter(dataAdapter);
        if (swipeRefreshLayout == null) swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        createData(args);
        return layout;
    }

    private void createData(final Bundle args){
        Observable.create(new Observable.OnSubscribe<DataModel>() {
            @Override
            public void call(Subscriber<? super DataModel> subscriber) {
                subscriber.onNext(ApiPresenter.getInstance().createData(args));
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
//                        LogToFile.e("DataFragment", "data size = " + data.getChildren().size());
                        notifyDataSetChange(data, mDataId);
                    }
                });

    }

    private Bundle setArgs(){
        Bundle args = new Bundle();
        args.putInt(ViewConstant.DATA_ID, mDataId);
        args.putLong(ViewConstant.FRAGMENT_ID, mFragmentId);
        args.putInt(ViewConstant.ACCOUNT_ID, 0);
        args.putBoolean(ViewConstant.SHOW_HIDE_FILE, true);
        return args;
    }

    @Override
    public void viewRefrush(DataModel dataModel, int dataId) {
        if (dataAdapter != null){
            notifyDataSetChange(dataModel, dataId);
        }
    }

    public boolean[] onBack(){
        final boolean[] isExit = {false};
        Observable.create(new Observable.OnSubscribe<DataModel>() {
            @Override
            public void call(Subscriber<? super DataModel> subscriber) {
                subscriber.onNext(ApiPresenter.getInstance().createData(getBackArgs()));
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
//                        LogToFile.e("DataFragment", "data size = " + data.getChildren().size());
                        notifyDataSetChange(data, mDataId);
                        isExit[0] = (data.getCurrentFolder().getPath().equals(data.getRootPath()));
                    }
                });
        return isExit;
    }

    private Bundle getBackArgs(){
        Bundle args = new Bundle();
        args.putInt(ViewConstant.DATA_ID, mDataId);
        args.putInt(ViewConstant.ACCOUNT_ID, mAccount);
        args.putLong(ViewConstant.FRAGMENT_ID, mFragmentId);
        args.putInt(ViewConstant.TYPE, ViewConstant.BACK);
        args.putInt(ViewConstant.SORT_TYPE, SortConstant.TITLE_ORDER);
        args.putInt(ViewConstant.SORT_ORDER,SortConstant.ASCENDING_ORDER);
        return args;
    }

    public boolean isExit(){
        if (mData.getCurrentFolder().getPath().equals(mData.getRootPath())){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DataActivity)getActivity()).setCurrentFragment(this);
    }

    public DataModel getmData() {
        return mData;
    }

    public int getmDataId() {
        return mDataId;
    }

    public long getmFragmentId() {
        return mFragmentId;
    }

    public int getmAccount(){
        return mAccount;
    }

    public void notifyDataSetChange(DataModel data, int dataId){
        this.mDataId = dataId;
        this.mData = data;
        String path = data.getCurrentFolder().getPath();
        (getActivity()).setTitle(path.substring(path.lastIndexOf("/") + 1));
        if (dataAdapter != null){
            if (data.childrenCount() == 0){
                emptyLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }else {
                emptyLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                dataAdapter.notifyDataSetChanged(dataId, data);
            }
        }
    }

    public void inserItem(int position, DataModel data, int dataId){
        mData = data;
        if (dataAdapter != null){
            dataAdapter.insertItem(dataId, data, position);
        }
    }

    public void refreshCount(Bundle args){
        notifyDataSetChange(ApiPresenter.getInstance().createData(args), args.getInt(ViewConstant.DATA_ID));
    }

    public DataAdapter getDataAdapter() {
        return dataAdapter;
    }

    public void notifyDataSetChange(){
        if (dataAdapter != null){
            dataAdapter.notifyDataSetChanged();
        }
    }

    public void onSwipLayoutRefresh() {
        swipeRefreshLayout.setRefreshing(true);


        Observable.create(new Observable.OnSubscribe<DataModel>() {
            @Override
            public void call(Subscriber<? super DataModel> subscriber) {
                subscriber.onNext(ApiPresenter.getInstance().createData(setRefreshCurrent()));
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
//                        LogToFile.e("DataFragment", "data size = " + data.getChildren().size());
                        swipeRefreshLayout.setRefreshing(false);
                        notifyDataSetChange(data, mDataId);
                    }
                });
    }

    private Bundle setRefreshCurrent(){
        Bundle args = new Bundle();
        args.putInt(ViewConstant.DATA_ID, mDataId);
        args.putInt(ViewConstant.ACCOUNT_ID, mAccount);
        args.putLong(ViewConstant.FRAGMENT_ID, mFragmentId);
        args.putInt(ViewConstant.TYPE,ViewConstant.REFRESH);
        args.putInt(ViewConstant.SORT_TYPE, SortConstant.TITLE_ORDER);
        args.putInt(ViewConstant.SORT_ORDER,SortConstant.ASCENDING_ORDER);
        return args;
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }

        Observable.create(new Observable.OnSubscribe<DataModel>() {
            @Override
            public void call(Subscriber<? super DataModel> subscriber) {
                subscriber.onNext(ApiPresenter.getInstance().createData(setRefreshCurrent()));
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
//                        LogToFile.e("DataFragment", "data size = " + data.getChildren().size());
                        swipeRefreshLayout.setRefreshing(false);
                        notifyDataSetChange(data, mDataId);
                    }
                });
    }
}
