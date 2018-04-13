package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FePrivateCloudFile;
import com.grt.filemanager.orm.dao.base.FePrivateCloudFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class FePrivateCloudFileHelper extends BaseHelper<FePrivateCloudFile, Long> {

    public FePrivateCloudFileHelper(AbstractDao dao) {
        super(dao);
    }

    public List<FePrivateCloudFile> listFiles(long fileId, int accountId) {
        return queryBuilder().where(FePrivateCloudFileDao.Properties.ParentId.eq(fileId),
                FePrivateCloudFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public List<FePrivateCloudFile> getFiles(String path, int accountId) {
        return queryBuilder().where(FePrivateCloudFileDao.Properties.Path.eq(path),
                        FePrivateCloudFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public FePrivateCloudFile getFile(String path, int accountId) {
        return queryBuilder().where(FePrivateCloudFileDao.Properties.Path.eq(path),
                FePrivateCloudFileDao.Properties.AccountId.eq(accountId)).unique();
    }

    public long hasFile(String path, int accountId) {
        return queryBuilder().where(FePrivateCloudFileDao.Properties.Path.eq(path),
                FePrivateCloudFileDao.Properties.AccountId.eq(accountId)).count();
    }

}
