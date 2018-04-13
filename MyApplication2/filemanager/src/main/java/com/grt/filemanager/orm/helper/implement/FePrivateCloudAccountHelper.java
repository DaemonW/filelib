package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FePrivateCloudAccount;
import com.grt.filemanager.orm.dao.base.FePrivateCloudAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class FePrivateCloudAccountHelper extends BaseHelper<FePrivateCloudAccount, Long> {

    public FePrivateCloudAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName() {
        List<FePrivateCloudAccount> accountList = queryAll();
        if (accountList != null && accountList.size() > 0) {
            return accountList.get(0).getAccount();
        }

        return "/";
    }

    public int getAccountId() {
        List<FePrivateCloudAccount> accountList = queryAll();
        if (accountList != null && accountList.size() > 0) {
            return accountList.get(0).getId().intValue();
        }

        return 0;
    }

    public void deleteAccount(int accountId) {
        queryBuilder().where(FePrivateCloudAccountDao.Properties.Id.eq(accountId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
