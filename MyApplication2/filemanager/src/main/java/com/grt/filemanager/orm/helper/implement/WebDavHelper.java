package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.WebDavFile;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class WebDavHelper extends BaseHelper<WebDavFile, Long> {

    public WebDavHelper(AbstractDao dao) {
        super(dao);
    }

}
