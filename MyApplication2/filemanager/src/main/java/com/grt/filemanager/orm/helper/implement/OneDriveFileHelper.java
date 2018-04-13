package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.OneDriveFile;
import com.grt.filemanager.orm.dao.base.OneDriveFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class OneDriveFileHelper extends BaseHelper<OneDriveFile, Long> {

    public OneDriveFileHelper(AbstractDao dao) {
        super(dao);
    }

    public List<OneDriveFile> queryFiles(String path, int accountId) {
        return queryBuilder().where(OneDriveFileDao.Properties.Path.eq(path),
                OneDriveFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public OneDriveFile queryFile(String path, int accountId) {
        return queryBuilder().where(OneDriveFileDao.Properties.Path.eq(path),
                OneDriveFileDao.Properties.AccountId.eq(accountId)).unique();
    }

}
