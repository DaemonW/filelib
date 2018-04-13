package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.BoxAccount;
import com.grt.filemanager.orm.dao.base.BoxAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class BoxAccountHelper extends BaseHelper<BoxAccount, Long> {
    public BoxAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName(int accountId) {
        BoxAccount account = queryBuilder()
                .where(BoxAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }

    public BoxAccount getAccount(int accountId) {
        return queryBuilder().where(BoxAccountDao.Properties.Id.eq(accountId)).unique();
    }

}
