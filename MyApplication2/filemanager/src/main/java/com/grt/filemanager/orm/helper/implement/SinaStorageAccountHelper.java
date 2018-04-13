package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SinaStorageAccount;
import com.grt.filemanager.orm.dao.base.SinaStorageAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SinaStorageAccountHelper extends BaseHelper<SinaStorageAccount, Long> {

    public SinaStorageAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public SinaStorageAccount getAccount(String accessKey) {
        return queryBuilder()
                .where(SinaStorageAccountDao.Properties.Token.eq(accessKey)).unique();
    }

    public String getAccountName(int accountId) {
        SinaStorageAccount account = queryBuilder()
                .where(SinaStorageAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }
}
