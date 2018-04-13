package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.YandexFile;
import com.grt.filemanager.orm.dao.base.YandexFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class YanDexFileHelper extends BaseHelper<YandexFile, Long> {

    public YanDexFileHelper(AbstractDao dao) {
        super(dao);
    }

    public void deleteChildren(String parentPath) {
        queryBuilder().where(YandexFileDao.Properties.Path.like(parentPath.concat("/%")))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
