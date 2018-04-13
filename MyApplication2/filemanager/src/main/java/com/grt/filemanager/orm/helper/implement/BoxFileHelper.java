package com.grt.filemanager.orm.helper.implement;


import android.text.TextUtils;

import com.grt.filemanager.orm.dao.BoxFile;
import com.grt.filemanager.orm.dao.base.BoxFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class BoxFileHelper extends BaseHelper<BoxFile, Long> {

    public BoxFileHelper(AbstractDao dao) {
        super(dao);
    }

    public BoxFile getFile(String path) {
        return queryBuilder().where(BoxFileDao.Properties.Path.eq(path)).unique();
    }

    public List<BoxFile> getFiles(int accountId, String path) {
        return queryBuilder().where(BoxFileDao.Properties.AccountId.eq(accountId),
                BoxFileDao.Properties.Path.eq(path)).list();
    }

    public List<BoxFile> getChildren(int accountId, String parentFileId) {
        return queryBuilder().where(BoxFileDao.Properties.AccountId.eq(accountId),
                BoxFileDao.Properties.ParentId.eq(parentFileId)).list();
    }

    public void deleteChildren(int accountId, String parentId) {
        queryBuilder().where(BoxFileDao.Properties.AccountId.eq(accountId),
                BoxFileDao.Properties.ParentId.eq(parentId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

    public void deleteChildren(String parentPath) {
        if (TextUtils.isEmpty(parentPath)) {
            queryBuilder().where(BoxFileDao.Properties.Path.like(parentPath.concat("/%")))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
        }
    }

}
