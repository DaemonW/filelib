package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SyncHistory;
import com.grt.filemanager.orm.dao.base.SyncHistoryDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class SyncHistoryHelper extends BaseHelper<SyncHistory, Long> {

    public SyncHistoryHelper(AbstractDao dao) {
        super(dao);
    }

    public List<SyncHistory> queryHistoryBySyncFlagAndTime(int syncFlag, long time) {
        return queryBuilder().where(SyncHistoryDao.Properties.SyncFlag.eq(syncFlag),
                SyncHistoryDao.Properties.HistoryFlag.ge(time)).list();
    }

    public void deleteBySyncFlag(int syncFlag) {
        queryBuilder().where(SyncHistoryDao.Properties.SyncFlag.eq(syncFlag))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
