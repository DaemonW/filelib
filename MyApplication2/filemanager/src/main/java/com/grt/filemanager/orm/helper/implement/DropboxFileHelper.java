package com.grt.filemanager.orm.helper.implement;

import android.text.TextUtils;

import com.grt.filemanager.orm.dao.DropboxFile;
import com.grt.filemanager.orm.dao.base.DropboxFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class DropboxFileHelper extends BaseHelper<DropboxFile, Long> {

    public DropboxFileHelper(AbstractDao dao) {
        super(dao);
    }

    public void deleteChildren(String parentPath) {
        if (!TextUtils.isEmpty(parentPath)) {
            queryBuilder().where(DropboxFileDao.Properties.Path.like(parentPath.concat("/%")))
                    .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
        }
    }

}
