package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.LabelFile;
import com.grt.filemanager.orm.dao.base.LabelFileDao;
import com.grt.filemanager.orm.helper.BaseHelper;
import com.grt.filemanager.orm.helper.DbUtils;

import de.greenrobot.dao.AbstractDao;

public class LabelFileHelper extends BaseHelper<LabelFile, Long> {


    public LabelFileHelper(AbstractDao dao) {
        super(dao);
    }


    public boolean isLabelFile(String path) {
        try {
            if (path == null) {
                return false;
            }
            LabelFile labelFile = queryBuilder()
                    .where(LabelFileDao.Properties.Path.eq(path))
                    .orderDesc(LabelFileDao.Properties.Date_modified).unique();
            if (labelFile != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public LabelFile getLabelFile(String path) {
        return queryBuilder()
                .where(LabelFileDao.Properties.Path.eq(path))
                .unique();
    }

    public void deleteLabelFile(long labelFileId) {
        DbUtils.getLabelFileHelper().queryBuilder()
                .where(LabelFileDao.Properties.Id.eq(labelFileId))
                .buildDelete().forCurrentThread()
                .executeDeleteWithoutDetachingEntities();
    }


    public void deleteLabelFile(String path) {
        DbUtils.getLabelFileHelper().queryBuilder()
                .where(LabelFileDao.Properties.Path.eq(path))
                .buildDelete().forCurrentThread()
                .executeDeleteWithoutDetachingEntities();
    }

}
