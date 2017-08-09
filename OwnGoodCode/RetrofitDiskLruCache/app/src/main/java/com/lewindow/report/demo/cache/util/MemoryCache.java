package com.lewindow.report.demo.cache.util;

import android.util.Log;
import android.util.LruCache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by james on 2017/8/8.
 */

public class MemoryCache {
    private static MemoryCache INSTANCE;

    private LruCache<String, byte[]> mLruCache;

    private List<String> keysCache = new ArrayList<>();

    public static MemoryCache getInstance() {
        if (INSTANCE == null) {
            synchronized (MemoryCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MemoryCache();

                }
            }
        }
        return INSTANCE;
    }

    private void addMemoryCache(String key, byte[] value) {
        getmLruCache().put(key, value);
        keysCache.add(key);
    }

    public void releaseAllMemoryCache() {
        for (String key : keysCache) {
            releaseMemoryCache(key);
        }

    }

    public void releaseMemoryCache(String key) {
        if (key != null) {
            if (getmLruCache().remove(key) != null) {
                Log.d(TAG.TAG, "releaseMemoryCache successed !  ");
            } else {
                Log.d(TAG.TAG, "releaseMemoryCache key Non-existent !");
            }
        }
    }

    public Observable<byte[]> writeToMemoryCache(final String key, final byte[] value) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                if (getmLruCache().get(key) == null) {
                    Log.i(TAG.TAG, "缓存到内存上了！");
                    addMemoryCache(key, value);
                } else {
                    Log.i(TAG.TAG, "内存已经存在无需缓存！");
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public Observable<byte[]> getMemoryCache(final String key) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> e) throws Exception {
                byte[] cache = getmLruCache().get(key);
                if (cache != null) {
                    Log.d(TAG.TAG, "从内存中读取出来的数据 ！ ");
                    e.onNext(cache);
                } else {
                    e.onComplete();
                }
            }
        });
    }

    private LruCache<String, byte[]> getmLruCache() {
        if (mLruCache == null) {
            mLruCache = new LruCache<String, byte[]>(getCacheMaxSize()) {
                @Override
                protected int sizeOf(String key, byte[] value) {
                    return value.length;
                }
            };
        }
        return mLruCache;
    }

    private int getCacheMaxSize() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        return maxMemory >> 3;
    }

}
