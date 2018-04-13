package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FragmentInfo;
import com.grt.filemanager.orm.dao.base.FragmentInfoDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class FragmentInfoHelper extends BaseHelper<FragmentInfo, Long> {

    public FragmentInfoHelper(AbstractDao dao) {
        super(dao);
    }

    public void deleteFragment(long fragmentId) {
        queryBuilder().where(FragmentInfoDao.Properties.FragmentId.eq(fragmentId))
                .buildDelete().forCurrentThread().executeDeleteWithoutDetachingEntities();
    }

}
