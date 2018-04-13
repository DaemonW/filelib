package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FeSmbFile;
import com.grt.filemanager.orm.dao.base.FeSmbFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SmbFileHelper extends BaseHelper<FeSmbFile, Long> {

    public SmbFileHelper(AbstractDao dao) {
        super(dao);
    }

    public void deleteFile(int accountId) {
        queryBuilder().where(FeSmbFileDao.Properties.AccountId.eq(accountId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
