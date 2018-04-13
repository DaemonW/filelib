package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.GCloudAccount;
import com.grt.filemanager.orm.dao.base.GCloudAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class GCloudAccountHelper extends BaseHelper<GCloudAccount, Long> {

    public GCloudAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName() {
        List<GCloudAccount> accountList = queryAll();
        if (accountList != null && accountList.size() > 0) {
            return accountList.get(0).getAccount();
        }

        return "/";
    }

    public int getAccountId() {
        List<GCloudAccount> accountList = queryAll();
        if (accountList != null && accountList.size() > 0) {
            return accountList.get(0).getId().intValue();
        }

        return -1;
    }

    public String getAccount() {
        List<GCloudAccount> accountList = queryAll();
        if (accountList == null || accountList.isEmpty()) {
            return null;
        }

        return accountList.get(accountList.size() - 1).getAccount();
    }

    public GCloudAccount getAccount(int accountId) {
        return queryBuilder().where(GCloudAccountDao.Properties.Id.eq(accountId)).unique();

    }

    public void deleteAccount(int accountId) {
        queryBuilder().where(GCloudAccountDao.Properties.Id.eq(accountId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
