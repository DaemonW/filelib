package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.orm.dao.DownloadedFile;
import com.grt.filemanager.orm.helper.BaseHelper;

import de.greenrobot.dao.AbstractDao;

public class DownloadedFileHelper extends BaseHelper<DownloadedFile, Long> {

    public DownloadedFileHelper(AbstractDao dao) {
        super(dao);
    }
}
