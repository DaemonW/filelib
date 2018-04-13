
package com.grt.filemanager.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LogToFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseData implements DataModel {
    private static String TAG = "BaseData";

    protected static Context context = ApiPresenter.getContext();
    private static DataComparator comparator;
    private static ExecutorService calcService;
    private static final int THREAD_NUM = 3;

    protected int mAccountId;
    protected String mRootPath;
    protected FileInfo mCurFile;
    protected List<FileInfo> mChildrenFiles;
    protected PathStack mPathStack;
    protected boolean mShowHideFile;


    //满足异步请求需求
    private static ArrayMap<Long, BaseData> dataClassMap = new ArrayMap<>();

    public static ArrayMap<Long, BaseData> getDataClassMap() {
        return dataClassMap;
    }

    /**
     * 数据源抽象基类构造函数.
     */
    public BaseData(int accountId) {
        this.mAccountId = accountId;
        this.mRootPath = getSubClassRootPath(accountId);
        this.mCurFile = getSubClassObject(mRootPath, accountId);
    }

    @Override
    public int getAccountId() {
        return mAccountId;
    }

    @Override
    public void setRootPath(String rootPath) {
        mRootPath = rootPath;
    }

    public void setCurrentFileInfo(FileInfo fileInfo) {
        this.mCurFile = fileInfo;
    }

    @Override
    public void setHideFileArg(boolean show) {
        mShowHideFile = show;
    }

    @Override
    public void setCurrentPath(int type, String path, int position, int sortType, int sortOrder) {
        switch (type) {
            case DataConstant.INIT_PATH:
                getPathStack().push(path, 0);
                break;

            case DataConstant.RESTORE_PATH:
                netQuickLogin();
                break;

            case DataConstant.KEEP_PATH:
                return;//DON`T DELETE

            case DataConstant.REINIT:
                setPathStack(PathStack.buildStackString(mRootPath, path));
                break;

            case DataConstant.GO_PATH:
                PathStack stack = getPathStack();
                stack.setPeekPosition(position);
                stack.push(path, position);
                break;

            case DataConstant.BACK_PATH:
                path = getPathStack().popAndPeekPath();
                break;

            case DataConstant.FASTBACK_PATH:
                path = getPathStack().popAndPeekPath(path);
                break;

            case DataConstant.REFRESH:
                path = getPathStack().peekPath();
                onPreRefresh(path);
                break;

            case DataConstant.SORT:
                sortChildren(mChildrenFiles, sortType, sortOrder);
                return;

            case DataConstant.FAST_FORWARD_PATH:
                fastForwardPathStack(path);
                break;

            default:
                return;
        }
        if (!TextUtils.isEmpty(path) && (type != DataConstant.GO_PATH)) {
            mCurFile = getFileInfo(path);
        }

        if (mChildrenFiles != null) {
            mChildrenFiles.clear();
        } else {
            mChildrenFiles = new ArrayList<>();
        }

        fillChildrenList(sortType, sortOrder);
    }

    protected void onPreRefresh(String path) {
    }

    protected void netQuickLogin() {
    }

    protected void fastForwardPathStack(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String peekPath = mPathStack.peekPath();
        if (peekPath.equals(path)) {
            return;
        }

        String forwardPath = peekPath.equals("/") ? path.substring(1) : path.replace(peekPath.concat("/"), "");
        String[] names = forwardPath.split("/");
        for (String name : names) {
            peekPath = FileUtils.concatPath(peekPath).concat(name);
            mPathStack.push(peekPath, 0);
        }
    }

    protected void fillChildrenList(int sortType, int sortOrder) {
//        LogToFile.e(TAG , "mCurFile size="+mCurFile.getList(mShowHideFile).size());
        mChildrenFiles.addAll(mCurFile.getList(mShowHideFile));
        sortChildren(mChildrenFiles, sortType, sortOrder);
    }

    protected void sortChildren(List<FileInfo> children, int sortType, int sortOrder) {
        if (children != null) {
            try {
//                表示使用老版本的排序方法，避免偶尔崩溃
                System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
                if (sortType > 0) {
                    Collections.sort(children, getComparator(sortType, sortOrder));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private DataComparator getComparator(int sortType, int sortOrder) {
        if (comparator == null) {
            comparator = new DataComparator(sortType, sortOrder);
        } else {
            comparator.updateKeys(sortType, sortOrder);
        }

        return comparator;
    }

    @Override
    public String getRootPath() {
        return mRootPath;
    }

    @Override
    public FileInfo getCurrentFolder() {
        return mCurFile;
    }

    @Override
    public FileInfo getCurrentChildren(int position) {
        if (mChildrenFiles == null) {

            if (mCurFile == null) {
                mCurFile = getFileInfo(mRootPath);
            }

            mChildrenFiles = new ArrayList<>();
            mChildrenFiles.addAll(mCurFile.getList(mShowHideFile));
        }

        if (mChildrenFiles.size() > position && (position > -1)) {
            return mChildrenFiles.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int childrenCount() {
        if (mChildrenFiles == null) {
            return 0;
        } else {
            return mChildrenFiles.size();
        }
    }

    @Override
    public PathStack getPathStack() {
        if (mPathStack == null) {
            mPathStack = new PathStack();
        }

        return mPathStack;
    }

    @Override
    public void setPathStack(String stack) {
        if (mPathStack == null) {
            mPathStack = new PathStack();
        }

        if (TextUtils.isEmpty(stack)) {
            mPathStack.push(mRootPath, 0);
        } else {
            mPathStack.restorePathStack(stack);
        }
    }

    @Override
    public FileInfo getFileInfo(String path) {
        return getSubClassObject(path, mAccountId);
    }

    @Override
    public void addChild(String path, int position) {
        if (position < 0) {
            mChildrenFiles.add(getSubClassObject(path, mAccountId));
        } else {
            mChildrenFiles.add(position, getSubClassObject(path, mAccountId));
        }
    }

    @Override
    public void addChild(FileInfo file, int position) {
        if (position < 0) {
            mChildrenFiles.add(file);
        } else {
            mChildrenFiles.add(position, file);
        }
    }

    @Override
    public void deleteChild(int position) {
        mChildrenFiles.remove(position);
    }

    /**
     * @param path          所需计算文件夹路径
     * @param includeFolder 是否包含文件夹
     * @return 文件夹内子文件个数及大小
     */
    @Override
    public Bundle calcChildrenSizeAndCounts(String path, boolean includeFolder) {
        long size;
        int count;
        ChildrenSizeAndCount info = new ChildrenSizeAndCount();

        FileInfo file = getSubClassObject(path, mAccountId);
        if (file.isFolder()) {

            calcFolderSizeAndCounts(file, info, includeFolder);
            size = info.getSize();
            count = includeFolder ? info.getCount() + 1 : info.getCount();

        } else {
            size = file.getSize();
            count = 1;
        }

        Bundle args = new Bundle();
        args.putLong(DataConstant.ALL_CHILDREN_SIZE, size);
        args.putInt(DataConstant.ALL_CHILDREN_COUNT, count);
        return args;
    }

    protected void calcFolderSizeAndCounts(FileInfo file, final ChildrenSizeAndCount info,
                                           final boolean includeFolder) {
        if (calcService == null) {
            calcService = Executors.newCachedThreadPool();
        }

        final ConcurrentLinkedQueue<FileInfo> qualifiedFileQueue = new ConcurrentLinkedQueue<>();
        qualifiedFileQueue.add(file);
        final AtomicInteger lock = new AtomicInteger(0);

        for (int i = 0; i < THREAD_NUM; i++) {
            calcService.submit(new Runnable() {
                @Override
                public void run() {
                    indexFilesMethod(qualifiedFileQueue, info, includeFolder);
                    if (lock.incrementAndGet() == THREAD_NUM) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            });
        }

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<FileInfo> getChildren() {
        return mChildrenFiles;
    }

    @Override
    public void filtrationChildren(int type) {
        boolean isFolder = type == DataConstant.FOLDER;

        if (mChildrenFiles != null) {
            int count = mChildrenFiles.size();
            FileInfo file;
            if (isFolder) {
                for (int index = count - 1; index >= 0; index--) {
                    file = mChildrenFiles.get(index);
                    if (!file.isFolder()) {
                        mChildrenFiles.remove(index);
                    }
                }
            } else {
                for (int index = count - 1; index >= 0; index--) {
                    file = mChildrenFiles.get(index);
                    if (file.isFolder()) {
                        mChildrenFiles.remove(index);
                    }
                }
            }
        }
    }

    public static List<FileInfo> addFiles(FileInfo rootFile,
                                          ConcurrentLinkedQueue<FileInfo> qualifiedFileQueue,
                                          List<FileInfo> fileLists) {
        if (rootFile.exists()) {
            List<FileInfo> files = rootFile.getList(true);
            for (int i = 0, count = files.size(); i < count; i++) {
                qualifiedFileQueue.add(files.get(i));
            }

            fileLists.add(rootFile);

            FileInfo childFile;
            while (!qualifiedFileQueue.isEmpty()) {
                childFile = qualifiedFileQueue.poll();
                if (childFile == null) {
                    continue;
                }

                fileLists.add(childFile);

                List<FileInfo> childrenFiles = null;
                if (childFile.isFolder()) {
                    childrenFiles = childFile.getList(true);
                }

                if (null == childrenFiles || childrenFiles.size() == 0) {
                    continue;
                }

                for (int i = 0, count = childrenFiles.size(); i < count; i++) {
                    childFile = childrenFiles.get(i);
                    if (childFile.isFolder()) {
                        qualifiedFileQueue.add(childFile);
                    } else {
                        fileLists.add(childFile);
                    }
                }
            }
        }
        return fileLists;
    }

    private void indexFilesMethod(ConcurrentLinkedQueue<FileInfo> qualifiedFileQueue,
                                  ChildrenSizeAndCount info, boolean includeFolder) {
        int length;
        int index;

        while (!qualifiedFileQueue.isEmpty()) {
            FileInfo dir = qualifiedFileQueue.poll();
            if (dir == null) {
                continue;
            }

            List<FileInfo> files = dir.getList(true);
            if (null == files || files.size() == 0) {
                continue;
            }

            length = files.size();
            for (index = 0; index < length / 4; index += 4) {
                addContent(qualifiedFileQueue, info, includeFolder, files.get(index));
                addContent(qualifiedFileQueue, info, includeFolder, files.get(index + 1));
                addContent(qualifiedFileQueue, info, includeFolder, files.get(index + 2));
                addContent(qualifiedFileQueue, info, includeFolder, files.get(index + 3));
            }

            for (; index < length; index++) {
                addContent(qualifiedFileQueue, info, includeFolder, files.get(index));
            }
        }
    }

    private void addContent(ConcurrentLinkedQueue<FileInfo> qualifiedFileQueue,
                            ChildrenSizeAndCount info, boolean includeFolder, FileInfo file) {
        if (includeFolder) {
            info.addCount();
        }

        info.addSize(file.getSize());
        if (file.isFolder()) {
            qualifiedFileQueue.add(file);
        }
    }

    protected class ChildrenSizeAndCount {
        AtomicInteger mCount;
        AtomicLong mSize;

        public ChildrenSizeAndCount() {
            mCount = new AtomicInteger(0);
            mSize = new AtomicLong(0);
        }

        public int getCount() {
            return mCount.get();
        }

        public long getSize() {
            return mSize.get();
        }

        public void addSize(long size) {
            mSize.addAndGet(size);
        }

        public void addCount() {
            mCount.incrementAndGet();
        }

        public void addCount(int count) {
            mCount.addAndGet(count);
        }

        public void clear() {
            mSize.set(0);
            mCount.set(0);
        }
    }

    protected abstract String getSubClassRootPath(int accountId);

    protected abstract FileInfo getSubClassObject(String path, int accountId);

}
