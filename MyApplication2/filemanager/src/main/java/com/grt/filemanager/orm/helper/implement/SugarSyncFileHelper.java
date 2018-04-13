package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SugarsyncFile;
import com.grt.filemanager.orm.dao.base.SugarsyncFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SugarSyncFileHelper extends BaseHelper<SugarsyncFile, Long> {

    public SugarSyncFileHelper(AbstractDao dao) {
        super(dao);
    }

    public SugarsyncFile getParent(String parentId, int accountId) {
        return queryBuilder()
                .where(SugarsyncFileDao.Properties.FileId.eq(parentId),
                        SugarsyncFileDao.Properties.AccountId.eq(accountId)).unique();
    }

}
