package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.YandexAccount;
import com.grt.filemanager.orm.dao.base.YandexAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class YanDexAccountHelper extends BaseHelper<YandexAccount, Long> {
    public YanDexAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName(int accountId) {
        YandexAccount account = queryBuilder()
                .where(YandexAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }
}
