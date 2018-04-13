package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.Statistics;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class StatisticsHelper extends BaseHelper<Statistics, Long> {

    public StatisticsHelper(AbstractDao dao) {
        super(dao);
    }
}
