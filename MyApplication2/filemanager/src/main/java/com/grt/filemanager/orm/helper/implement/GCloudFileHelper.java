package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.GCloudFile;
import com.grt.filemanager.orm.dao.base.GCloudFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class GCloudFileHelper extends BaseHelper<GCloudFile, Long> {

    public GCloudFileHelper(AbstractDao dao) {
        super(dao);
    }

    public GCloudFile queryFile(String path) {
        return queryBuilder().where(GCloudFileDao.Properties.Path.eq(path)).unique();
    }

}
