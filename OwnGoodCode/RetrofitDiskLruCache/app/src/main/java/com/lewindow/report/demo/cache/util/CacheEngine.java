package com.lewindow.report.demo.cache.util;

import com.lewindow.report.demo.cache.net.DownLoadMgr;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by james on 2017/8/8.
 */

public class CacheEngine {
    private static CacheEngine INSTANCE;

    public CacheEngine() {
    }

    public static CacheEngine getInstance() {
        if (INSTANCE == null) {
            synchronized (CacheEngine.class) {
                INSTANCE = new CacheEngine();
            }
        }
        return INSTANCE;
    }


    public Observable<byte[]> getLocalImg(final String srcPath) {
        Observable<byte[]> memoryCache = MemoryCache.getInstance().getMemoryCache(srcPath);
        Observable<byte[]> diskCache = DiskCache.getInstance().getDiskCache(srcPath);
        Observable<byte[]> downLoadFile = DownLoadMgr.getInstance().downLoadFileByUrl(srcPath);
        return Observable.concat(memoryCache, diskCache, downLoadFile);
    }

    public Observable<byte[]> getLocalImgs(final List<String> srcPaths) {
        return Observable.fromArray(srcPaths.toArray()).flatMap(new Function<Object, Observable<byte[]>>() {
            @Override
            public Observable<byte[]> apply(Object url) throws Exception {
                return getLocalImg((String) url);
            }
        });
    }

}
