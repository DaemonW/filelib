package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.SugarsyncAccount;
import com.grt.filemanager.orm.dao.base.SugarsyncAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SugarSyncAccountHelper extends BaseHelper<SugarsyncAccount, Long> {

    public SugarSyncAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public SugarsyncAccount getAccount(int accountId) {
        SugarsyncAccount account = queryBuilder()
                .where(SugarsyncAccountDao.Properties.Id.eq(accountId)).unique();

        if (account == null) {
            return null;
        }
        return account;
    }

    public String getAccountName(int accountId) {
        SugarsyncAccount account = queryBuilder()
                .where(SugarsyncAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }

    public SugarsyncAccount getAccount(String emailAddress) {
        return queryBuilder()
                .where(SugarsyncAccountDao.Properties.Account.eq(emailAddress)).unique();
    }

}
