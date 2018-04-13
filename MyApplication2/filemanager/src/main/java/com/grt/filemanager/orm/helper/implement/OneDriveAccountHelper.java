package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.OneDriveAccount;
import com.grt.filemanager.orm.dao.base.OneDriveAccountDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;
public class OneDriveAccountHelper extends BaseHelper<OneDriveAccount, Long> {

    public OneDriveAccountHelper(AbstractDao dao) {
        super(dao);
    }

    public String getAccountName(int accountId) {
        OneDriveAccount account = queryBuilder()
                .where(OneDriveAccountDao.Properties.Id.eq(accountId)).unique();
        if (account != null) {
            return account.getAccount();
        }

        return "/";
    }

}
