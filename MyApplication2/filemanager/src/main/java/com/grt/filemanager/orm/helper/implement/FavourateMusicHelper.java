package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.FavourateMusic;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

/**
 * Function:音乐收藏
 */
public class FavourateMusicHelper extends BaseHelper<FavourateMusic, Long> {
    public FavourateMusicHelper(AbstractDao dao) {
        super(dao);
    }
}
