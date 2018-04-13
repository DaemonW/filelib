package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.RecentMusic;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

/**
 * Function:最近播放音乐
 */
public class RecentMusicHelper extends BaseHelper<RecentMusic, Long> {
    public RecentMusicHelper(AbstractDao dao) {
        super(dao);
    }
}
