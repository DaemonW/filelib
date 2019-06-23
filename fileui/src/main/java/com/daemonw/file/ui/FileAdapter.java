package com.daemonw.file.ui;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.daemonw.file.core.model.Filer;
import com.daemonw.file.core.model.LocalFile;
import com.daemonw.file.ui.util.RxUtil;
import com.daemonw.widget.CommonAdapter;
import com.daemonw.widget.ItemViewDelegate;
import com.daemonw.widget.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

class FileAdapter extends CommonAdapter<Filer> {
    private String mRootPath;
    private Filer mCurrent;
    private FileComparator mFileComparator = new FileComparator();
    private boolean mMultiSelect;
    private Set<Filer> mSelected = new HashSet<>();
    private FileLoader loader;

    private static SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd hh:mm:ss", Locale.getDefault());

    FileAdapter(Activity context, String rootPath, int mountType, FileLoader loader) {
        super(context, new ArrayList<Filer>());
        mCurrent = new LocalFile(context, rootPath, mountType);
        mRootPath = mCurrent.getPath();
        addItemViewDelegate(new ItemViewDelegate<Filer>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.file_item;
            }

            @Override
            public boolean isMatchedType(Filer item, int position) {
                return true;
            }

            @Override
            public void convert(ViewHolder holder, Filer filer, int position) {
                FileAdapter.this.convert(holder, filer, position);
            }
        });
        this.loader = loader;
        load(mCurrent, loader);
    }

    void setMultiSelect(boolean enable) {
        if (!enable) {
            clearSelect();
        }
        mMultiSelect = enable;
    }

    boolean isMultiSelect() {
        return mMultiSelect;
    }

    @Override
    protected void convert(ViewHolder holder, final Filer file, int position) {
        holder.setText(R.id.file_name, file.getName());
        holder.setText(R.id.file_modified, formater.format(new Date(file.lastModified())));
        if (file.isDirectory()) {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_folder);
        } else {
            holder.setImageResource(R.id.file_icon, R.drawable.ic_file);
        }
        CheckBox checkBox = holder.getView(R.id.file_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                file.setChecked(isChecked);
                if (isChecked) {
                    mSelected.add(file);
                } else {
                    mSelected.remove(file);
                }
            }
        });
        if (mMultiSelect) {
            checkBox.setChecked(file.isChecked());
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    void loadParent() {
        if (isRoot()) {
            return;
        }
        mCurrent = mCurrent.getParentFile();
        load(mCurrent, loader);
    }

    void load(Filer file) {
        load(file, loader);
    }

    void reload() {
        load(mCurrent, loader);
    }

    boolean isRoot() {
        return mCurrent.getPath().equals(mRootPath);
    }

    Filer getCurrent() {
        return mCurrent;
    }

    Set<Filer> getSelected() {
        return mSelected;
    }

    void clearSelect() {
        for (Filer f : mSelected) {
            f.setChecked(false);
        }
        mSelected.clear();
    }

    private Filer[] sortFile(Filer[] fileList) {
        if (fileList == null) {
            return null;
        }
        Arrays.sort(fileList, mFileComparator);
        return fileList;
    }

    private void addFiles(Filer[] files) {
        if (files == null || files.length == 0) {
            return;
        }
        Collections.addAll(mDatas, files);
    }


    private void load(final Filer f, final FileLoader fileLoader) {
        RxUtil.add(Single.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (fileLoader != null) {
                            fileLoader.onLoad();
                        }
                    }
                }).observeOn(Schedulers.io())
                .map(new Function<Integer, Filer[]>() {
                    @Override
                    public Filer[] apply(Integer integer) throws Exception {
                        if (fileLoader != null) {
                            Filer[] files = fileLoader.load(f);
                            if (files == null) {
                                files = new Filer[0];
                            }
                            return files;
                        }
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Filer[]>() {
                    @Override
                    public void accept(Filer[] files) throws Exception {
                        mCurrent = f;
                        mSelected.clear();
                        mDatas.clear();
                        addFiles(sortFile(files));
                        notifyDataSetChanged();
                        if (fileLoader != null) {
                            fileLoader.onLoadFinish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (fileLoader != null) {
                            fileLoader.onError(throwable);
                        }
                    }
                }));
    }

    class FileComparator implements Comparator<Filer> {
        @Override
        public int compare(Filer f1, Filer f2) {
            if (f1.isDirectory() == f2.isDirectory()) {
                return f1.getName().toUpperCase().compareTo(f2.getName().toUpperCase());
            }
            return f1.isDirectory() ? -1 : 1;
        }
    }
}
