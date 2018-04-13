package com.grt.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.grt.filemanager.adapter.StateHandler;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.constant.SortConstant;
import com.grt.filemanager.fragment.DataFragment;
import com.grt.filemanager.model.DataModel;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.model.RefrushData;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.view.OperationViewHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LDC on 2018/3/19.
 */

public class DataActivity extends AppCompatActivity {

    private DataFragment currentFragment;
    private static StateHandler stateHandler = StateHandler.getInstance();
    private IntentHandler intentHandler;
    private ActivityResultHandler mActivityResultHandler;
    private ActionBar toolbar;

    public void setCurrentFragment(DataFragment fragment){
        currentFragment = fragment;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        toolbar = getSupportActionBar();
        ApiPresenter.getInstance().initActivity(this);
        intentHandler = new IntentHandler();
        intentHandler.setActivity(this);
        intentHandler.handleIntent(getIntent());
        mActivityResultHandler = ActivityResultHandler.initInstance();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        ApiPresenter.getInstance().initActivity(this);
        if (intentHandler != null){
            intentHandler.handleIntent(intent);
        }
    }

    @Subscribe
    public void finishActivity(Integer type){
//        LogToFile.e("DataActivity ", "adsgadg type=" +type);
        if (type == ViewConstant.OTG_REMOVED){
            finish();
        }
    }
    public ActionBar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !isFinishing()){
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments){
                if (fragment.isVisible() && fragment instanceof DataFragment){
                    DataFragment dataFragment = (DataFragment) fragment;
                    if (dataFragment.isExit()){
                        if (stateHandler.getSelectedItemCount() > 0){//取消选中状态
                            stateHandler.resetState();
                            currentFragment.notifyDataSetChange();
                            return true;
                        }
                    }else {
                        if (stateHandler.getSelectedItemCount() >0){
                            if (stateHandler.getIsCopyState()){//返回上一级
                                dataFragment.onBack();
                                return true;
                            }else {//取消选中状态
                                stateHandler.resetState();
                                currentFragment.notifyDataSetChange();
                                return true;
                            }
                        }else {//返回上一级
                            dataFragment.onBack();
                            return true;
                        }
                    }
                }
            }
        }
        stateHandler.resetState();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_data_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.create_new_folder) {
            ApiPresenter.getInstance().createFileOrFolder( this, true, currentFragment.getmData(),
                    currentFragment.getmDataId(), currentFragment.getmFragmentId());
        } else if (i == R.id.paste) {//目前粘贴操作只适应于当前页面内操作
            if (stateHandler.getIsCopyState()){
                DataModel data = currentFragment.getmData();
                Set<String> selectPaths = stateHandler.getPathArray();
                List<FileInfo> selectFiles = new ArrayList<>();//选中的文件
                for (String selectPath : selectPaths){
                    FileInfo file = data.getFileInfo(selectPath);
                    selectFiles.add(file);
                }
                ApiPresenter.getInstance().paste(data, data,currentFragment.getmDataId(),currentFragment.getmDataId(), selectFiles);
            }
        } else if (i == R.id.delete) {//删除
            OperationViewHelper.createDeleteDialog(this, currentFragment.getmDataId(), stateHandler.getSelectedItemList(), currentFragment.getmData());
        } else if (i == R.id.selected) {//选中返回文件或者文件夹信息即操作接口,stateHandler里面包含所有选中文件
            Set<String> selectPaths = stateHandler.getPathArray();//当前选中文件路径
            stateHandler.resetState();
            finish();

        }else if (i == R.id.create_new_file){
            ApiPresenter.getInstance().createFileOrFolder( this, false, currentFragment.getmData(),
                    currentFragment.getmDataId(), currentFragment.getmFragmentId());
        }else if (i == R.id.copy){//复制
            if (stateHandler.getSelectedItemCount() > 0){
                stateHandler.setCopyState(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onFragmentRefrush(RefrushData data){
        int type = data.getType();
        int result = data.getResult();
        switch (type){
            case ViewConstant.CREATE_FILE_OR_FOLDER:
                if (data.getResult() == ResultConstant.SUCCESS){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.folder_created), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
                }
                if (currentFragment != null && currentFragment.isVisible()){
                    int childCount = data.getData().childrenCount();
                    DataModel dataModel = data.getData();
                    if (childCount == 0){
                        currentFragment.refreshCount(setRefreshCurrent(currentFragment));
                    }else {
                        dataModel.addChild(data.getPath(), childCount);
                        currentFragment.inserItem(childCount, dataModel, data.getDataId());
                    }
                    currentFragment.notifyDataSetChange(data.getData(), data.getDataId());
                }
                break;
            case ViewConstant.DELETE_FILE_OR_FOLDER:
                if (result == ResultConstant.SUCCESS){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.operation_success), Toast.LENGTH_SHORT).show();
                }else if (result == ResultConstant.FAILED){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
                }
                stateHandler.resetState();
                currentFragment.refreshCount(setRefreshCurrent(currentFragment));
                break;
            case ViewConstant.PASTE:
                if (result == ResultConstant.SUCCESS){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.operation_success), Toast.LENGTH_SHORT).show();
                }else if (result == ResultConstant.FAILED){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
                }else if (result == ResultConstant.EXIST){
                    Toast.makeText(DataActivity.this, DataActivity.this.getString(R.string.same_name), Toast.LENGTH_SHORT).show();
                }
                currentFragment.refreshCount(setRefreshCurrent(currentFragment));
                stateHandler.resetState();
                break;
        }
    }

    private Bundle setRefreshCurrent(DataFragment current){
        Bundle args = new Bundle();
        args.putInt(ViewConstant.DATA_ID, DataConstant.STORAGE_DATA_ID);
        args.putInt(ViewConstant.ACCOUNT_ID, current.getmAccount());
        args.putLong(ViewConstant.FRAGMENT_ID, current.getmFragmentId());
        args.putInt(ViewConstant.TYPE,ViewConstant.REFRESH);
        args.putInt(ViewConstant.SORT_TYPE, SortConstant.TITLE_ORDER);
        args.putInt(ViewConstant.SORT_ORDER,SortConstant.ASCENDING_ORDER);
        return args;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mActivityResultHandler != null){
            boolean result = mActivityResultHandler.handleResult(requestCode, resultCode, data);
            if (result){
                getWindow().getDecorView().post(new Runnable() {
                    @Override
                    public void run() {
                        intentHandler.handleIntent(new Intent().putExtras(setOTGBundle()));
                    }
                });
            }
        }
    }

    private Bundle setOTGBundle() {
        Bundle args = new Bundle();
        args.putInt(ViewConstant.DATA_ID, DataConstant.STORAGE_DATA_ID);
        args.putInt(ViewConstant.ACCOUNT_ID, 0);
        args.putInt(ViewConstant.TYPE,ViewConstant.INIT);
        args.putInt(ViewConstant.SORT_TYPE, SortConstant.TITLE_ORDER);
        args.putInt(ViewConstant.SORT_ORDER,SortConstant.ASCENDING_ORDER);
        args.putBoolean(ViewConstant.SHOW_HIDE_FILE, false);
        args.putString(ViewConstant.DATA_TYPE, ViewConstant.OTG);
        args.putInt(ViewConstant.NEW_INTENT_ACTION, ViewConstant.DISPLAY_DATA);
        return args;
    }
}
