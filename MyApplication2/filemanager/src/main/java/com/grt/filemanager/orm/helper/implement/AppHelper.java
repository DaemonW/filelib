package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.App;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class AppHelper extends BaseHelper<App, Long> {

    public AppHelper(AbstractDao dao) {
        super(dao);
    }

}
