package com.lewindow.report.demo.cache.net;

import android.util.Log;

import com.lewindow.report.demo.cache.util.DiskCache;
import com.lewindow.report.demo.cache.util.MemoryCache;
import com.lewindow.report.demo.cache.util.TAG;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by james on 2017/8/8.
 */

public class DownLoadMgr {

    private static DownLoadMgr INSTANCE;

    public static DownLoadMgr getInstance() {
        if (INSTANCE == null) {
            synchronized (DownLoadMgr.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownLoadMgr();
                }
            }
        }
        return INSTANCE;
    }

    public Observable<byte[]> downLoadFileByUrl(final String url) {
        return Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(final ObservableEmitter<byte[]> e) throws Exception {
                FileDownLoadService service = ServiceGenerator.createService(FileDownLoadService.class);
                Call<ResponseBody> call = service.downLoadFileSync(url);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG.TAG, "down load file URL : " + url);
                        if (response.isSuccessful()) {
                            Log.d(TAG.TAG, "down load file successed !");

                            try {
                                byte[] file = response.body().bytes();
                                e.onNext(file);
                                //write cache
                                writeToCache(url, file);

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            Log.d(TAG.TAG, "down load file failed ! ");
                            e.onComplete();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d(TAG.TAG, "down load file failed : url= " + url);
                    }
                });

            }
        }).subscribeOn(Schedulers.newThread());
    }

    private void writeToCache(final String url, final byte[] file) {
        Observable<byte[]> memoryCache = MemoryCache.getInstance().writeToMemoryCache(url, file);
        Observable<byte[]> diskCache = DiskCache.getInstance().writeToDiskCache(url, file);
        Observable.concat(memoryCache, diskCache).subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) throws Exception {
                Log.i(TAG.TAG, "save file to memoryCache & diskCache: ");
            }
        });
    }

}
