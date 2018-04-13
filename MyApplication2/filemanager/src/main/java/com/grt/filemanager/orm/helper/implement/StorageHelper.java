package com.grt.filemanager.orm.helper.implement;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.orm.dao.Storage;
import com.grt.filemanager.orm.dao.base.StorageDao;
import com.grt.filemanager.orm.helper.BaseHelper;
import com.grt.filemanager.util.SearchResultHelper;

import java.io.File;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class StorageHelper extends BaseHelper<Storage, Long>
        implements SearchResultHelper<Storage> {

    private static final Context context = ApiPresenter.getContext();

    public StorageHelper(AbstractDao dao) {
        super(dao);
    }

    @Override
    public void batchInsert(List<Storage> searchResult) {
        saveOrUpdate(searchResult);
    }

    @Override
    public Storage getContentObject(File file) {
        return initStorage(file.isDirectory(), file.getPath());
    }

    public Storage buildObject(FileInfo file) {
        return initStorage(file.isFolder(), file.getPath());
    }

    private Storage initStorage(boolean isFolder, String path) {

        if (isFolder) {
            Storage storage = new Storage();
            storage.setPath(path);

            String[] projection = new String[]{"sum(" + MediaStore.Files.FileColumns.SIZE + "),"
                    + " count(*)"};
            String selection = MediaStore.Files.FileColumns.DATA + " like ?";
            String[] selectionArgs = new String[]{path + "/%"};

            Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    projection, selection, selectionArgs, null);
            if (cursor != null) {
                cursor.moveToFirst();
                storage.setChildrenSize(cursor.getLong(0));
                storage.setChildrenCount(cursor.getInt(1));
            }

            if (cursor != null) {
                cursor.close();
            }

            return storage;
        }

        return null;
    }

    public Storage getStorage(String path) {
        return queryBuilder().where(StorageDao.Properties.Path.eq(path)).unique();
    }

}
