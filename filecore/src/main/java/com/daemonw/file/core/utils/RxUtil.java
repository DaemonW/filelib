package com.daemonw.file.core.utils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxUtil {
    private static CompositeDisposable mDisposable = new CompositeDisposable();

    public static void add(Disposable disposable) {
        mDisposable.add(disposable);
    }
}
