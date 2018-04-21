package com.grt.filelib;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.daemonw.filelib.R;
import com.daemonw.filelib.model.Filer;
import com.daemonw.fileui.activity.FileActivity;

import java.util.Iterator;
import java.util.Set;

public class FileManagerActivity extends FileActivity {

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
                Set<Filer> selected = getSelected();
                Iterator<Filer> iterator = selected.iterator();
                while (iterator.hasNext()) {
                    iterator.next().delete();
                }
                break;
            case R.id.action_copy:
                break;
            case R.id.action_cut:
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
