package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.WebDavAccount;
import com.grt.filemanager.orm.dao.base.WebDavAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class WebDavAccountHelper extends BaseHelper<WebDavAccount, Long> {

    public WebDavAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName(int accountId) {
        WebDavAccount account = queryBuilder()
                .where(WebDavAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }

    public WebDavAccount getAccount(int accountId) {
        return queryBuilder()
                .where(WebDavAccountDao.Properties.Id.eq(accountId)).unique();
    }


    public long getAccountId(String hostname, int port, String username) {
        WebDavAccount account = queryBuilder().where(WebDavAccountDao.Properties.Hostname.eq(hostname),
                WebDavAccountDao.Properties.Port.eq(port),
                WebDavAccountDao.Properties.Account.eq(username)).unique();
        if (account != null) {
            return account.getId();
        }

        return 0;
    }
}
