package com.daemonw.fileui.core;

import com.daemonw.filelib.model.Filer;

import java.util.List;

public interface OnFileChooseListener {
    void onFileSelect(List<Filer> selected);
}
