package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.DropboxAccount;
import com.grt.filemanager.orm.dao.base.DropboxAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class DropboxAccountHelper extends BaseHelper<DropboxAccount, Long> {

    public DropboxAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName(int accountId) {
        DropboxAccount account = queryBuilder()
                .where(DropboxAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }
}
