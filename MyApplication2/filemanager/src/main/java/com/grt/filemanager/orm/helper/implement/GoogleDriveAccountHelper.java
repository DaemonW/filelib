package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.GoogleDriveAccount;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class GoogleDriveAccountHelper extends BaseHelper<GoogleDriveAccount, Long> {

    public GoogleDriveAccountHelper(AbstractDao dao) {
        super(dao);
    }

}
