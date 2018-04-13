package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.RecentOpen;
import com.grt.filemanager.orm.dao.base.RecentOpenDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class RecentOpenHelper extends BaseHelper<RecentOpen, Long> {

    public RecentOpenHelper(AbstractDao dao) {
        super(dao);
    }

    public RecentOpen getFile(String path) {
        return queryBuilder().where(RecentOpenDao.Properties.Path.eq(path)).unique();
    }

}
