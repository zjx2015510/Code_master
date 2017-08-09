package com.lewindow.report.demo.cache.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by james on 2017/8/8.
 */

public interface FileDownLoadService {
    // option 1: a resource relative to your base URL
    @GET("youname/2.jpg")
    Call<ResponseBody> downLoadFileSync();

    // option 2: using a dynamic URL
    @GET
    Call<ResponseBody> downLoadFileSync(@Url String fileUrl);
}
