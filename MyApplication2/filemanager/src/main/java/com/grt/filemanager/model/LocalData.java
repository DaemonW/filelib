package com.grt.filemanager.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.model.imp.local.archive.ArchiveFactory;
import com.grt.filemanager.orm.dao.LabelFile;
import com.grt.filemanager.orm.dao.Storage;
import com.grt.filemanager.orm.dao.base.LabelFileDao;
import com.grt.filemanager.orm.dao.base.StorageDao;
import com.grt.filemanager.orm.helper.DbUtils;
import com.grt.filemanager.orm.helper.implement.SearchHelper;
import com.grt.filemanager.orm.helper.implement.StorageHelper;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.MediaUtils;
import com.grt.filemanager.util.StorageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LDC on 2018/3/12.
 */

public abstract class LocalData extends BaseData implements LocalModel{

    private final AtomicBoolean mStartTmpQueue;
    private ConcurrentLinkedQueue<FileInfo> mTmpFileQueue;
    private static ExecutorService dBService;
    private final static int TMP_CACHE_COUNT = 50;
    /**
     * 数据源抽象基类构造函数.
     *
     * @param accountId
     */
    public LocalData(int accountId) {
        super(accountId);
        this.mStartTmpQueue = new AtomicBoolean(false);
    }

    @Override
    public void addDbCache(FileInfo file, String oldPath, boolean finished) {
        if (file != null) {
            MediaUtils.updateSystemMedia(context, file.getPath());

//            DbUtils.getSearchHelper().saveOrUpdate(DbUtils.getSearchHelper().buildObject(file));
//            if (oldPath != null) {
//                insertNewLabelFile(oldPath, file);
//            }
        }
    }

    @Override
    public void addDbCache(FileInfo file) {
        if (!mStartTmpQueue.get()) {
            mStartTmpQueue.set(true);
            if (mTmpFileQueue == null) {
                mTmpFileQueue = new ConcurrentLinkedQueue<>();
            }

            if (dBService == null) {
                dBService = Executors.newFixedThreadPool(30);
            }

            synchronized (mStartTmpQueue) {
                for (int i = 0; i < 20; i++) {
                    final int finalI = i;
                    dBService.execute(new Runnable() {
                        @Override
                        public void run() {
                            addCacheDb(mStartTmpQueue, mTmpFileQueue, DbUtils.getSearchHelper());
                        }
                    });
                }
            }
        }

        if (file != null) {
            mTmpFileQueue.add(file);
        }
    }

    @Override
    public void stopDBCacheService() {

    }

    @Override
    public void deleteDbCache(String path, boolean isFolder) {
        deleteSystemFileDb(context, path, isFolder);
        if (!isFolder){
            MediaUtils.deleteMediaStore(path);
        }
    }

    @Override
    public void updateDbCache(String oldPath, FileInfo file) {

    }

    @Override
    public void clearDbCache() {

    }

    @Override
    public List<FileInfo> doSearch(int searchDataId, String... keyword) {
        return null;
    }

    @Override
    public void recursionUpdateFolder(String path) {

    }

    @Override
    public void
    updateAllParent(String path, long addSize, int addCount) {
        Storage storage;
        Bundle args;
        StorageHelper helper = DbUtils.getStorageHelper();

        if (addSize < 0) {
            args = StorageUtils.getChildrenLengthAndCount(path);
            addSize = args.getLong(DataConstant.ALL_CHILDREN_SIZE);
            addCount = args.getInt(DataConstant.ALL_CHILDREN_COUNT);
            helper.saveOrUpdate(new Storage(null, path, addSize, addCount));
            path = FileUtils.getParentPath(path);
        }

        while (!path.equals(mRootPath)) {
            storage = helper.queryBuilder()
                    .where(StorageDao.Properties.Path.eq(path)).unique();

            if (storage == null) {
                storage = new Storage();
                args = StorageUtils.getChildrenLengthAndCount(path);
                storage.setChildrenCount(args.getInt(DataConstant.ALL_CHILDREN_COUNT));
                storage.setChildrenSize(args.getLong(DataConstant.ALL_CHILDREN_SIZE));
            }

            storage.setChildrenCount(storage.getChildrenCount() + addCount);
            storage.setChildrenSize(storage.getChildrenSize() + addSize);
            updateDirInfo(path, storage.getChildrenCount(), storage.getChildrenSize());
            helper.saveOrUpdate(storage);

            path = FileUtils.getParentPath(path);
            if ("/".equals(path)) {
                break;
            }
        }
    }

    @Override
    public void deleteAllParentCache(String path, long size, int count) {

    }

    @Override
    public boolean isPortableStorage() {
        return false;
    }

    @Override
    public void addDbCacheForArchive(FileInfo file) {

    }

    private void updateDirInfo(String parentPath, int count, long size) {
        Bundle info = new Bundle();
        info.putInt(DataConstant.ALL_CHILDREN_COUNT, count);
        info.putLong(DataConstant.ALL_CHILDREN_SIZE, size);

        DirInfo.getDirInfo().put(parentPath, info);
    }

    private void insertNewLabelFile(final String oldPath, final FileInfo file) {
        LabelFile labelFile = DbUtils.getLabelFileHelper().queryBuilder().where(LabelFileDao.Properties.Path.eq(oldPath)).unique();
        if (labelFile != null) {
            if (!file.getPath().equals(labelFile.getPath())) {
                LabelFile newLabelFile = new LabelFile();
                newLabelFile.setName(file.getName());
                newLabelFile.setPath(file.getPath());
                newLabelFile.setLabelId(labelFile.getLabelId());
                newLabelFile.setMime_type(labelFile.getMime_type());
                newLabelFile.setIs_file(labelFile.getIs_file());
                newLabelFile.setDate_modified(labelFile.getDate_modified());
                newLabelFile.setSize(labelFile.getSize());
                DbUtils.getLabelFileHelper().saveOrUpdate(newLabelFile);
            }
        }
    }

    private static void addCacheDb(AtomicBoolean start, ConcurrentLinkedQueue<FileInfo> queue,
                                   SearchHelper helper) {
        String[] pathArray = new String[TMP_CACHE_COUNT];
        List<FileInfo> fileList = new ArrayList<>();
        FileInfo file;
        String path;
        int count = 0;

        while (start.get() || !queue.isEmpty()) {
            file = queue.poll();
            if (file == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            path = file.getPath();
            pathArray[count++] = path;
            fileList.add(file);

            if (count < TMP_CACHE_COUNT) {
                continue;
            }

            MediaUtils.updateSystemMedia(pathArray);
//            helper.saveOrUpdate(helper.buildList(fileList));

            count = 0;
            fileList.clear();
        }

        if (count > 0) {
            MediaUtils.updateSystemMedia(pathArray);
//            helper.saveOrUpdate(helper.buildList(fileList));
        }
    }

    @Override
    public ArchiveFileInfo getFileInfo(String path, String password, boolean createNew) {
        return ArchiveFactory.getArchiveFile(path, password, createNew);
    }

    private void deleteSystemFileDb(Context context, String path, boolean isFolder) {
        Uri uri = MediaStore.Files.getContentUri("external");
        String where = MediaStore.Files.FileColumns.DATA + " = ?";
        String[] selectionArgs = new String[]{path};
        if (isFolder) {
            where = MediaStore.Files.FileColumns.DATA + " like ?";
            selectionArgs = new String[]{path + "/%"};
            context.getContentResolver().delete(uri, where, selectionArgs);
        }
        context.getContentResolver().delete(uri, where, selectionArgs);
    }
}
