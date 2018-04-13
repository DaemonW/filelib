package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SyncInfo;
import com.grt.filemanager.orm.dao.base.SyncInfoDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class
SyncInfoHelper extends BaseHelper<SyncInfo, Long> {

    public SyncInfoHelper(AbstractDao dao) {
        super(dao);
    }

    public SyncInfo queryUnique(int syncFlag, String insidePath) {
        return queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.eq(insidePath)).unique();
    }

    public List<SyncInfo> query(int syncFlag, String insidePath) {
        return queryBuilder()
                .where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                        SyncInfoDao.Properties.InsidePath.eq(insidePath)).list();
    }

    public void delete(int syncFlag, String insidePath) {
        queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.eq(insidePath)).buildDelete()
                .forCurrentThread().executeDeleteWithoutDetachingEntities();

        queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.like(insidePath.concat("/%"))).buildDelete()
                .forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

    public void delete(int syncFlag) {
        queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag)).buildDelete()
                .forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

    public String queryVersion(int syncFlag, String insidePath) {
        SyncInfo info = queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.eq(insidePath)).unique();
        return info != null ? info.getVersion() : "";
    }

    public boolean existed(int syncFlag, String insidePath) {
        SyncInfo info = queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.eq(insidePath)).unique();
        return info != null && info.getExisted();
    }

    public void rename(int syncFlag, String insidePath, String newInsidePath, String version) {
        SyncInfo syncInfo = queryBuilder().where(SyncInfoDao.Properties.SyncFlag.eq(syncFlag),
                SyncInfoDao.Properties.InsidePath.eq(insidePath)).unique();
        if (syncInfo != null) {
            syncInfo.setInsidePath(newInsidePath);
            saveOrUpdate(syncInfo);
        }
    }

}
