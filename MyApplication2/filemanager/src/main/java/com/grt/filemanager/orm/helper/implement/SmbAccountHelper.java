package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SmbAccount;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SmbAccountHelper extends BaseHelper<SmbAccount, Long> {
    public SmbAccountHelper(AbstractDao dao) {
        super(dao);
    }

}