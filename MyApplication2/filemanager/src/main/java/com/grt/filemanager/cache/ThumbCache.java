package com.grt.filemanager.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.util.SecurityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;

public class ThumbCache {

    private static ThumbCache instance;

    private static final Context context = ApiPresenter.getContext();

    //软引用
    private static final int SOFT_CACHE_CAPACITY = 40;
    private static final int HARD_CACHE_SIZE = 10 * 1024 * 1024;
    private static final int DISK_CACHE_SIZE = 32 * 1024 * 1024;
    private static final int ICON_MEM_SIZE = 4 * 1024 * 1024;

    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT
            = Bitmap.CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;
    private static final int DISK_CACHE_INDEX = 0;

    private LruCache<String, Bitmap> sHardThumbCache;

    private LruCache<String, Bitmap> mIconMemoryCache;
    private final Object lock = new Object();

    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;

    public static ThumbCache getCache() {
        if (instance == null) {
            instance = new ThumbCache();
        }
        return instance;
    }

    private ThumbCache() {
        createThumbCache();
        createAppIconCache();
        createDiskCache();
    }

    private void createThumbCache() {
        this.sHardThumbCache = new LruCache<String, Bitmap>(HARD_CACHE_SIZE) {
            @Override
            public int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区
                sSoftThumbCache.put(key, new SoftReference<>(oldValue));
            }
        };
    }

    private static final LinkedHashMap<String, SoftReference<Bitmap>> sSoftThumbCache =
            new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true) {
                @Override
                public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
                    return super.put(key, value);
                }

                @Override
                protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                    return size() > SOFT_CACHE_CAPACITY;
                }
            };

    //缓存bitmap
    public boolean putThumb(String key, Bitmap bitmap, boolean isList) {
        if (bitmap != null) {
            synchronized (lock) {
                sHardThumbCache.put(key, bitmap);
            }
            return true;
        }
        return false;
    }

    //从缓存中获取bitmap
    public Bitmap getThumb(String key, boolean isList) {
        synchronized (lock) {
            final Bitmap bitmap = sHardThumbCache.get(key);
            if (bitmap != null)
                return bitmap;
        }
        //硬引用缓存区间中读取失败，从软引用缓存区间读取
        synchronized (sSoftThumbCache) {
            SoftReference<Bitmap> bitmapReference = sSoftThumbCache.get(key);
            if (bitmapReference != null) {
                final Bitmap bitmap2 = bitmapReference.get();
                if (bitmap2 != null)
                    return bitmap2;
                else {
                    sSoftThumbCache.remove(key);
                }
            }
        }
        return null;
    }

    private void createAppIconCache() {
        this.mIconMemoryCache = new LruCache<String, Bitmap>(ICON_MEM_SIZE) {
            @Override
            public int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                //硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区
                sSoftIconCache.put(key, new SoftReference<>(oldValue));
            }
        };
    }

    private static final LinkedHashMap<String, SoftReference<Bitmap>> sSoftIconCache =
            new LinkedHashMap<String, SoftReference<Bitmap>>(SOFT_CACHE_CAPACITY, 0.75f, true) {
                @Override
                public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
                    return super.put(key, value);
                }

                @Override
                protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                    return size() > SOFT_CACHE_CAPACITY;
                }
            };

    //缓存bitmap
    public boolean putIconToMem(String key, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (lock) {
                mIconMemoryCache.put(key, bitmap);
            }
            return true;
        }
        return false;
    }

    //从缓存中获取bitmap
    public Bitmap getIconFromMem(String key) {
        synchronized (lock) {
            final Bitmap bitmap = mIconMemoryCache.get(key);
            if (bitmap != null)
                return bitmap;
        }
        //硬引用缓存区间中读取失败，从软引用缓存区间读取
        synchronized (sSoftIconCache) {
            SoftReference<Bitmap> bitmapReference = sSoftIconCache.get(key);
            if (bitmapReference != null) {
                final Bitmap bitmap2 = bitmapReference.get();
                if (bitmap2 != null)
                    return bitmap2;
                else {
                    sSoftIconCache.remove(key);
                }
            }
        }
        return null;
    }

    private void createDiskCache() {
        // Set up disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache.isClosed()) {
                File diskCacheDir = getDiskCacheDir(context, "netThumbCache");
                if (diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs();
                    }

                    if (diskCacheDir.getUsableSpace() > DISK_CACHE_SIZE) {
                        try {
                            mDiskLruCache = DiskLruCache.open(
                                    diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            mDiskCacheStarting = false;
            mDiskCacheLock.notifyAll();
        }
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     *
     * @param key    Unique identifier for the bitmap to store
     * @param bitmap The bitmap to store
     */
    public void putThumbToMemAndDisk(String key, Bitmap bitmap, boolean isList) {
        if (key == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        putThumb(key, bitmap, isList);

        synchronized (mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {
                key = SecurityUtils.md5String(key);
                OutputStream out = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot == null) {
                        final DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX);
                            bitmap.compress(
                                    DEFAULT_COMPRESS_FORMAT, DEFAULT_COMPRESS_QUALITY, out);
                            editor.commit();
                            out.close();
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Get from disk cache.
     *
     * @param key Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getThumbFromDisk(String key) {
        key = SecurityUtils.computeMd5(key);
        synchronized (mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDiskLruCache != null) {
                InputStream inputStream = null;
                try {
                    final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                    if (snapshot != null) {
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        if (inputStream != null) {
                            return BitmapFactory.decodeStream(inputStream);
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    /**
     * Clears both the memory and disk cache associated with this ImageCache object. Note that
     * this includes disk access so this should not be executed on the main/UI thread.
     */
    public void clearCache() {
        synchronized (mDiskCacheLock) {
            mDiskCacheStarting = true;
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                    Log.d("", "Disk cache cleared");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mDiskLruCache = null;
                createDiskCache();
            }
        }
    }

    public void clearMemoryCache() {
        if (sHardThumbCache != null) {
            sHardThumbCache.evictAll();
        }

        if (sSoftThumbCache != null) {
            sSoftThumbCache.clear();
        }

        if (mIconMemoryCache != null) {
            mIconMemoryCache.evictAll();
        }
    }

    public void clearDiskCache() {
        try {
            DiskLruCache.deleteContents(getDiskCacheDir(context, "netThumbCache"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Flushes the disk cache associated with this ImageCache object. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    public void flush() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                    Log.d("", "Disk cache flushed");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Closes the disk cache associated with this ImageCache object. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    public void close() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    if (!mDiskLruCache.isClosed()) {
                        mDiskLruCache.close();
                        mDiskLruCache = null;
                        Log.d("", "Disk cache closed");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据传入的uniqueName获取硬盘缓存的路径地址。
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        return new File(context.getCacheDir().getPath() + File.separator + uniqueName);
    }

}
