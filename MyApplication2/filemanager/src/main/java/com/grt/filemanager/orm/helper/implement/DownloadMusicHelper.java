package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.DownloadMusic;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

/**
 * Function:音乐下载
 */
public class DownloadMusicHelper extends BaseHelper<DownloadMusic, Long> {
    public DownloadMusicHelper(AbstractDao dao) {
        super(dao);
    }
}
