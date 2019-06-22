package com.daemonw.file.ui;

import com.daemonw.file.core.model.Filer;

import java.util.List;

public interface OnFileChooseListener {
    void onChooseFile(List<Filer> selected);
}
