package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.MixCloudFile;
import com.grt.filemanager.orm.dao.base.MixCloudFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class MixCloudFileHelper extends BaseHelper<MixCloudFile, Long> {

    public MixCloudFileHelper(AbstractDao dao) {
        super(dao);
    }

    public List<MixCloudFile> getList(String path, int accountId) {
        return queryBuilder().where(MixCloudFileDao.Properties.Path.eq(path),
                MixCloudFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public List<MixCloudFile> getListByParent(String parentId, int accountId) {
        return queryBuilder().where(MixCloudFileDao.Properties.ParentId.eq(parentId),
                MixCloudFileDao.Properties.AccountId.eq(accountId)).list();
    }

    public MixCloudFile getFile(String path, int accountId) {
        return queryBuilder().where(MixCloudFileDao.Properties.Path.eq(path),
                MixCloudFileDao.Properties.AccountId.eq(accountId)).unique();
    }

    public MixCloudFile getFileById(String fileId, int accountId) {
        return queryBuilder().where(MixCloudFileDao.Properties.FileId.eq(fileId),
                MixCloudFileDao.Properties.AccountId.eq(accountId)).unique();
    }

    public void deleteFileById(String fileId, int accountId) {
        queryBuilder().where(MixCloudFileDao.Properties.FileId.eq(fileId),
                MixCloudFileDao.Properties.AccountId.eq(accountId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

    public void deleteChildrenById(String parentId, int accountId) {
        queryBuilder().where(MixCloudFileDao.Properties.ParentId.eq(parentId),
                MixCloudFileDao.Properties.AccountId.eq(accountId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

    public void deleteChildren(String parentPath, int accountId) {
        queryBuilder().where(MixCloudFileDao.Properties.Path.like(parentPath.concat("/%")))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
