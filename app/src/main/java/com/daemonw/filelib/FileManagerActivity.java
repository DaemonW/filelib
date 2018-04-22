package com.daemonw.filelib;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.daemonw.filelib.model.Filer;
import com.daemonw.filelib.utils.FileUtil;
import com.daemonw.fileui.activity.FileActivity;

import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends FileActivity {
    private List<Filer> mChoosed = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mkdir:
                try {
                    getCurrent().mkDir("test_mkdir");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_mkfile:
                try {
                    getCurrent().createNewFile("test_mkfile");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_delete:
                mChoosed.clear();
                mChoosed.addAll(getSelected());
                for (Filer f : mChoosed) {
                    f.delete();
                }
                break;
            case R.id.action_erase:
                mChoosed.clear();
                mChoosed.addAll(getSelected());
                for (Filer f : mChoosed) {
                    FileUtil.delete(f, 1);
                }
                break;
            case R.id.action_copy:
                mChoosed.clear();
                mChoosed.addAll(getSelected());
                break;
            case R.id.action_cut:
                mChoosed.clear();
                mChoosed.addAll(getSelected());
                break;
            case R.id.action_paste:
                break;
            default:
                break;
        }
        refresh();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
