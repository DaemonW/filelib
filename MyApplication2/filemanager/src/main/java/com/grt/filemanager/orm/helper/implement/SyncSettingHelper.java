package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.SyncSetting;
import com.grt.filemanager.orm.dao.base.SyncSettingDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class SyncSettingHelper extends BaseHelper<SyncSetting, Long> {

    public SyncSettingHelper(AbstractDao dao) {
        super(dao);
    }

    public long getSyncSetting(int syncFlag, String key, long defaultValues) {
        SyncSetting setting = queryBuilder()
                .where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag),
                        SyncSettingDao.Properties.KeyName.eq(key))
                .unique();

        long values;
        if (setting != null) {
            values = Long.valueOf(setting.getValue());
        } else {
            saveOrUpdate(new SyncSetting(null, syncFlag, key, String.valueOf(defaultValues)));
            values = defaultValues;
        }

        return values;
    }

    public int getSyncSetting(int syncFlag, String key, int defaultValues) {
        SyncSetting setting = queryBuilder()
                .where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag),
                        SyncSettingDao.Properties.KeyName.eq(key))
                .unique();

        int values;
        if (setting != null) {
            values = Integer.valueOf(setting.getValue());
        } else {
            saveOrUpdate(new SyncSetting(null, syncFlag, key, String.valueOf(defaultValues)));
            values = defaultValues;
        }

        return values;
    }

    public String getSyncSetting(int syncFlag, String key, String defaultValues) {
        SyncSetting setting = queryBuilder()
                .where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag),
                        SyncSettingDao.Properties.KeyName.eq(key))
                .unique();

        String values;
        if (setting != null) {
            values = setting.getValue();
        } else {
            saveOrUpdate(new SyncSetting(null, syncFlag, key, defaultValues));
            values = defaultValues;
        }

        return values;
    }

    public int getSyncSetting(int syncFlag, String key) {
        SyncSetting setting = queryBuilder()
                .where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag),
                        SyncSettingDao.Properties.KeyName.eq(key))
                .unique();

        int values = 0;
        if (setting != null) {
            values = Integer.valueOf(setting.getValue());
        }

        return values;
    }

    public String getSyncSettingS(int syncFlag, String key) {
        SyncSetting setting = queryBuilder()
                .where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag),
                        SyncSettingDao.Properties.KeyName.eq(key))
                .unique();

        String values = null;
        if (setting != null) {
            values = setting.getValue();
        }

        return values;
    }

    public List<SyncSetting> getSyncSetting(String key) {
        return queryBuilder().where(SyncSettingDao.Properties.KeyName.eq(key)).list();
    }

    public void deleteBySyncFlag(int syncFlag) {
        queryBuilder().where(SyncSettingDao.Properties.SyncFlag.eq(syncFlag))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
