package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FtpAccount;
import com.grt.filemanager.orm.dao.base.FtpAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class FtpAccountHelper extends BaseHelper<FtpAccount, Long> {

    public FtpAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public int interAndGetAccountId(FtpAccount ftpAccount) {
        long accountId;
        saveOrUpdate(ftpAccount);
        ftpAccount = queryBuilder().
                where(FtpAccountDao.Properties.Serveraddress.eq(ftpAccount.getServeraddress()),
                        FtpAccountDao.Properties.Port.eq(ftpAccount.getPort()),
                        FtpAccountDao.Properties.Account.eq(ftpAccount.getAccount()))
                .list().get(0);
        accountId = ftpAccount.getId();
        return (int) accountId;
    }

    public FtpAccount getDbExitAccount(String ip, int port, int isAnonymous, String username, String password) {
        return queryBuilder().
                where(FtpAccountDao.Properties.Serveraddress.eq(ip),
                        FtpAccountDao.Properties.Port.eq(port),
                        FtpAccountDao.Properties.Anonymous.eq(isAnonymous),
                        FtpAccountDao.Properties.Account.eq(username),
                        FtpAccountDao.Properties.Password.eq(password)).unique();
    }

    public FtpAccount getInfoByAccountId(int accountId) {
        return queryBuilder().where(FtpAccountDao.Properties.Id.eq(accountId)).unique();
    }

    public int getTypeByAccountId(int accountId) {
        return queryBuilder().where(FtpAccountDao.Properties.Id.eq(accountId)).unique().getType();
    }

    public FtpAccount getFtpAccountByAccountId(int accountId) {
        return queryBuilder().where(FtpAccountDao.Properties.Id.eq(accountId)).unique();
    }

    public String getAccountName(int accountId) {
        FtpAccount account = queryBuilder().where(FtpAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            if (account.getAnonymous() == 0) {
                return account.getServeraddress();
            } else {
                return account.getAccount();
            }
        }

        return "/";
    }

}
