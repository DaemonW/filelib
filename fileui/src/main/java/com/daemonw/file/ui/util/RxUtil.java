package com.daemonw.file.ui.util;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxUtil {
    private static CompositeDisposable mDisposable = new CompositeDisposable();

    public static void add(Disposable disposable) {
        mDisposable.add(disposable);
    }

    public static void dispose() {
        mDisposable.dispose();
    }
}
