package com.lewindow.report.demo.cache.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by james on 2017/8/7.
 */

public class DiskCache {
    private Context context;
    private DiskLruCache mDiskLruCache = null;
    public static DiskCache INSTANCE;
    private static final int _MB = 1024 * 1024;

    private List<String> keysCache = new ArrayList<>();

    public static DiskCache getInstance() {
        if (INSTANCE == null) {
            synchronized (DiskCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DiskCache();
                }
            }
        }
        return INSTANCE;
    }

    public void open(Context context) {
        this.context = context;

        File cacheDir = getDiskCacheDir(context, "bitmap");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        try {
            mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * _MB);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void releaseAllDiskCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                keysCache.clear();

            }
        }
    }

    public boolean releaseDiskCache(String key) {
        if (mDiskLruCache != null) {
            try {
                keysCache.remove(key);
                return mDiskLruCache.remove(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Observable<byte[]> writeToDiskCache(final String srcPath, final byte[] obj) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                String keyForDisk = hashKeyForDisk(srcPath);

                BufferedInputStream in = null;
                BufferedOutputStream out = null;
                try {
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(keyForDisk);
                    if (snapshot == null) {
                        Log.i(TAG.TAG, "写入到硬盘上了");
                        keysCache.add(keyForDisk);

                        DiskLruCache.Editor edit = mDiskLruCache.edit(keyForDisk);

                        in = new BufferedInputStream(new ByteArrayInputStream(obj), 8 * 1024);
                        OutputStream outputStream = edit.newOutputStream(0);
                        out = new BufferedOutputStream(outputStream, 8 * 1024);
                        int len;
                        while ((len = in.read()) != -1) {
                            out.write(len);
                        }
                        edit.commit();

                    } else {
                        Log.i(TAG.TAG, "数据已存在硬盘，不需要再次写入");
                    }
                    emitter.onNext(obj);
                    emitter.onComplete();

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }


            }
        }).subscribeOn(Schedulers.newThread());
    }


    public Observable<byte[]> getDiskCache(final String srcPath) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter<byte[]> emitter) throws Exception {
                try {
                    Log.d(TAG.TAG, "getDiskCache() method currentThreadName: " + Thread.currentThread().getName());
                    String keyForDisk = hashKeyForDisk(srcPath);
                    DiskLruCache.Snapshot snapshot = mDiskLruCache.get(keyForDisk);
                    if (snapshot != null) {
                        Log.d(TAG.TAG, "从硬盘中读取出来的数据 ！ ");
                        InputStream is = snapshot.getInputStream(0);
                        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                        byte[] buff = new byte[100];
                        int line = 0;
                        while ((line = is.read(buff, 0, buff.length)) > 0) {
                            swapStream.write(buff, 0, line);
                        }
                        byte[] in2b = swapStream.toByteArray();
                        emitter.onNext(in2b);

                        //write to memory
                        MemoryCache.getInstance().writeToMemoryCache(srcPath, in2b).subscribe();

                    } else {
                        emitter.onComplete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    emitter.onComplete();
                }

            }
        });
    }

    /**
     * 获取缓存位置
     *
     * @param context
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 用来将字符串进行MD5编码
     *
     * @param key
     * @return
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
