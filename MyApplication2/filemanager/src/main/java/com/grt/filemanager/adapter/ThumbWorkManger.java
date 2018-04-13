package com.grt.filemanager.adapter;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;


import com.grt.filemanager.R;
import com.grt.filemanager.cache.ThumbCache;
import com.grt.filemanager.model.FileInfo;
import com.grt.filemanager.orm.dao.LabelFile;
import com.grt.filemanager.orm.helper.DbUtils;
import com.grt.filemanager.orm.helper.implement.LabelFileHelper;
import com.grt.filemanager.util.AppUtils;
import com.grt.filemanager.util.FeThumbUtils;
import com.grt.filemanager.util.TimeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * <p/>
 * 管理文件上所有图标
 */
public class ThumbWorkManger {

    private static ThumbWorkManger instance;

    private ExecutorService mThumbPool = null;

    public static ThumbWorkManger getInstance() {
        if (instance == null) {
            instance = new ThumbWorkManger();
        }
        return instance;
    }

    private ThumbWorkManger() {
        this.mThumbPool = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>());
    }

    private static class BaseThumbThread implements Comparable<BaseThumbThread> {

        private long mTimeFlag;

        public BaseThumbThread() {
            this.mTimeFlag = TimeUtils.getCurrentTime();
        }

        @Override
        public int compareTo(@NonNull BaseThumbThread another) {
            return (int) another.getTimeFlag() - (int) mTimeFlag;
        }

        public long getTimeFlag() {
            return mTimeFlag;
        }
    }

    public void getLocalThumb(BaseViewHolder holder,
                              FileInfo fileInfo, boolean isList) {
        mThumbPool.execute(new LocalThumbThread(holder, fileInfo, isList));
    }

    private class LocalThumbThread extends BaseThumbThread implements Runnable {

        private BaseViewHolder mHolder;
        private FileInfo mFileInfo;
        private boolean mIsList;

        public LocalThumbThread( BaseViewHolder holder,
                                FileInfo fileInfo, boolean isList) {
            super();
            this.mHolder = holder;
            this.mFileInfo = fileInfo;
            this.mIsList = isList;
        }

        @Override
        public void run() {
            setLocalThumb(mFileInfo, mIsList, mHolder);
        }
    }

    private void setLocalThumb(FileInfo fileInfo, final boolean isList,
                               final BaseViewHolder holder) {
        Bitmap bitmap = fileInfo.getThumb(isList);
        final String thumbKey = String.valueOf(isList)+ fileInfo.getPath();

        if (bitmap != null) {
            ThumbCache.getCache().putThumb(thumbKey, bitmap, isList);

            Observable.just(bitmap)
                    .filter(new Func1<Bitmap, Boolean>() {
                        @Override
                        public Boolean call(Bitmap bitmap) {
                            return thumbKey.equals(holder.getThumbView().getTag());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            setThumbImpl(holder, isList, bitmap);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            ThumbCache.getCache().clearMemoryCache();
                        }
                    });
        }
    }

    private void setThumbImpl(BaseViewHolder holder, boolean isList, Bitmap cacheBitmap) {
        try {
            if (null != cacheBitmap) {
                String name = "";
                if(null != holder.getFileInfo()) {
                    name = holder.getFileInfo().getName();
                }

                holder.getThumbCircleLayout().setVisibility(View.GONE);
                if (isList) {
                    holder.getThumbView().setImageBitmap(cacheBitmap);
                    FeThumbUtils.setFileThumbAlpha(name, holder.getThumbView());
                } else {
                    holder.getThumbView().setVisibility(View.GONE);
                    holder.getBigThumbView().setVisibility(View.VISIBLE);
                    holder.getBigThumbView().setImageBitmap(cacheBitmap);
                    FeThumbUtils.setFileThumbAlpha(name, holder.getBigThumbView());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAppIcon(String name, ImageView appIcon) {
        mThumbPool.execute(new IconThread(name, appIcon));
    }

    private class IconThread extends BaseThumbThread implements Runnable {

        private String mName;
        private ImageView mAppIcon;

        public IconThread(String name, ImageView appIcon) {
            super();
            this.mName = name;
            this.mAppIcon = appIcon;
        }

        @Override
        public void run() {
            Observable.just(mAppIcon)
                    .map(new Func1<ImageView, Bitmap>() {
                        @Override
                        public Bitmap call(ImageView imageView) {
                            if (imageView != null) {
                                Drawable drawable = AppUtils.getAppDrawable(mName);
                                if (drawable != null) {
                                    return ((BitmapDrawable) drawable).getBitmap();
                                }
                            }

                            return null;
                        }
                    })
                    .filter(new Func1<Bitmap, Boolean>() {
                        @Override
                        public Boolean call(Bitmap bitmap) {
                            return bitmap != null &&
                                    ThumbCache.getCache().putIconToMem(mName, bitmap) &&
                                    mAppIcon.getTag().equals(mName);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Bitmap>() {
                        @Override
                        public void call(Bitmap bitmap) {
                            mAppIcon.setImageBitmap(bitmap);
                            mAppIcon.setVisibility(View.VISIBLE);
                            FeThumbUtils.setFileThumbAlpha(mName.replace("/", ""), mAppIcon);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
        }
    }

    public void getLabelIcon(String name, ListViewHolder holder) {
        mThumbPool.execute(new LabelIconThread(name, holder));
    }

    private class LabelIconThread extends BaseThumbThread implements Runnable {

        private String mPath;
        private ListViewHolder mHolder;

        public LabelIconThread(String path, ListViewHolder holder) {
            super();
            this.mPath = path;
            this.mHolder = holder;
        }

        @Override
        public void run() {
            final ImageView labelIconView = mHolder.getLabelIcon();
            if (labelIconView != null) {

                LabelFileHelper helper = DbUtils.getLabelFileHelper();
                if (helper.isLabelFile(mPath)) {

                    LabelFile labelFile;
                    try {
                        labelFile = helper.getLabelFile(mPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    if (labelFile != null) {
                        long labelId = labelFile.getLabelId();
                        int colorId;
                        try {
                            colorId = DbUtils.getLabelHelper().queryColor(labelId);
                        } catch (Exception e) {
                            return;
                        }

                        Observable.just(colorId)
                                .filter(new Func1<Integer, Boolean>() {
                                    @Override
                                    public Boolean call(Integer integer) {
                                        return labelIconView.getTag().equals(mPath);
                                    }
                                })
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Integer>() {
                                    @Override
                                    public void call(Integer colorId) {
                                        if (colorId == -1) {
                                            labelIconView.setImageResource(R.drawable.ic_tag_collection_small);
                                        } else {
                                            labelIconView.setImageResource(colorId);
                                        }
                                        labelIconView.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                }
            }
        }
    }

    public void getNetThumb(String thumbKey, BaseViewHolder holder, FileInfo fileInfo, boolean isList) {
        mThumbPool.execute(new NetThumbThread(thumbKey, holder, fileInfo, isList));
    }

    private class NetThumbThread extends BaseThumbThread implements Runnable {

        private String mThumbKey;
        private BaseViewHolder mHolder;
        private FileInfo mFileInfo;
        private boolean mIsList;

        public NetThumbThread(String thumbKey, BaseViewHolder holder,
                              FileInfo fileInfo, boolean isList) {
            this.mThumbKey = thumbKey;
            this.mHolder = holder;
            this.mFileInfo = fileInfo;
            this.mIsList = isList;
        }

        @Override
        public void run() {
            Bitmap bitmap = ThumbCache.getCache().getThumbFromDisk(mThumbKey);
            if (bitmap == null) {
                bitmap = mFileInfo.getThumb(true);
            }

            if (bitmap != null) {
                ThumbCache.getCache().putThumbToMemAndDisk(mThumbKey, bitmap, true);

                Observable.just(bitmap)
                        .delay(100, TimeUnit.MILLISECONDS)
                        .filter(new Func1<Bitmap, Boolean>() {
                            @Override
                            public Boolean call(Bitmap bitmap) {
                                return mThumbKey.equals(mHolder.getThumbView().getTag());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                setThumbImpl(mHolder, mIsList, bitmap);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                ThumbCache.getCache().clearMemoryCache();
                            }
                        });
            }
        }

    }

}
