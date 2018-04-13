package com.grt.filemanager.orm.helper.implement;

import android.text.TextUtils;

import com.grt.filemanager.orm.dao.SinaStorageFile;
import com.grt.filemanager.orm.dao.base.SinaStorageFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class SinaStorageFileHelper extends BaseHelper<SinaStorageFile, Long> {

    public SinaStorageFileHelper(AbstractDao dao) {
        super(dao);
    }

    public void deleteChildren(String parentPath) {
        if (!TextUtils.isEmpty(parentPath)) {
            queryBuilder().where(SinaStorageFileDao.Properties.Path.like(parentPath.concat("/%")))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
        }
    }

}
