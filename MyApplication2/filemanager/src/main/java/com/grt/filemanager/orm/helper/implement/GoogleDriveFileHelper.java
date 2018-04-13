package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.GoogleDriveFile;
import com.grt.filemanager.orm.dao.base.GoogleDriveFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class GoogleDriveFileHelper extends BaseHelper<GoogleDriveFile, Long> {

    public GoogleDriveFileHelper(AbstractDao dao) {
        super(dao);
    }

    public List<GoogleDriveFile> queryFiles(String path, int accountId) {
        return queryBuilder().where(GoogleDriveFileDao.Properties.Path.eq(path),
                GoogleDriveFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public GoogleDriveFile queryFile(String path, int accountId) {
        return queryBuilder().where(GoogleDriveFileDao.Properties.Path.eq(path),
                GoogleDriveFileDao.Properties.AccountId.eq(accountId)).unique();
    }



}
