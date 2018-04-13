package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.FtpFileInfo;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class FtpFileInfoHelper extends BaseHelper<FtpFileInfo, Long> {

    public FtpFileInfoHelper(AbstractDao dao) {
        super(dao);
    }

    public void insertFilesInfo(List<FtpFileInfo> infoList){
        saveOrUpdate(infoList);
    }
}
