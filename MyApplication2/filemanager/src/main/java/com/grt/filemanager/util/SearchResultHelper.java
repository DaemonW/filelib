package com.grt.filemanager.util;

import java.io.File;
import java.util.List;
public interface SearchResultHelper<T> {

    void batchInsert(List<T> searchResult);

    T getContentObject(File file);
}
