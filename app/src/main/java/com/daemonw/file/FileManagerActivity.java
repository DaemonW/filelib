package com.daemonw.file;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.utils.FileUtil;
import com.daemonw.file.ui.activity.FileActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagerActivity extends FileActivity {
    private List<Filer> mChoosed = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mkdir:
                showCreateDirDialog();
                break;
            case R.id.action_mkfile:
                showCreateFileDialog();
                break;
            case R.id.action_delete:
                mChoosed.clear();
                mChoosed.addAll(getSelected());
                for (Filer f : mChoosed) {
                    FileUtil.deleteFile(f, 0);
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

    private void showCreateDirDialog() {
        EditText text = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(text)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString();
                        if (!name.isEmpty()) {
                            try {
                                getCurrent().mkDir(name);
                                refresh();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        dialog.show();
    }

    private void showCreateFileDialog() {
        EditText text = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(text)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = text.getText().toString();
                        if (!name.isEmpty()) {
                            try {
                                getCurrent().createNewFile(name);
                                refresh();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .setNegativeButton("cancel", null)
                .create();
        dialog.show();
    }
}
