package com.grt.filemanager.orm.helper.implement;


import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.orm.dao.Search;
import com.grt.filemanager.orm.helper.BaseHelper;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.SearchResultHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class SearchHelper extends BaseHelper<Search, Long>
        implements SearchResultHelper<Search> {

    public SearchHelper(AbstractDao dao) {
        super(dao);
    }

    @Override
    public void batchInsert(List<Search> searchResult) {
        saveOrUpdate(searchResult);
    }

    @Override
    public Search getContentObject(File file) {
        return initSearch(file.isDirectory(), file.length(), file.getPath(),
                file.getName(), file.lastModified());
    }

    public Search buildObject(FileInfo file) {
        return initSearch(file.isFolder(), file.getSize(), file.getPath(),
                file.getName(), file.getLastModified());
    }

    @Override
    public List<Search> buildList(List<FileInfo> fileArray) {
        if (fileArray == null) {
            return null;
        }

        ArrayList<Search> list = new ArrayList<>();
        for (FileInfo file : fileArray) {
            list.add(buildObject(file));
        }

        return list;
    }

    private Search initSearch(boolean isFolder, long size, String path,
                              String name, long lastModified) {
        Search search = new Search();

        if (isFolder) {
            search.setIsFolder(true);
            search.setSize((long) 0);
            search.setMimeType(DataConstant.MIME_TYPE_FOLDER);
        } else {
            search.setIsFolder(false);
            search.setSize(size);
            search.setMimeType(FileUtils.getMiMeType(name));
        }

        search.setName(name);
        search.setPath(path);
        search.setLastModified(lastModified);
        search.setSuffix(FileUtils.getFileSuffix(name));

        return search;
    }
}
