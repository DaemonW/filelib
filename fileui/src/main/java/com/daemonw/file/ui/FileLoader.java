package com.daemonw.file.ui;

import com.daemonw.file.core.model.Filer;

public interface FileLoader {

    Filer[] load(Filer f) throws Exception;

    void onLoad();

    void onLoadFinish();

    void onError(Throwable err);
}
