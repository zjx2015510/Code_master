package com.lewindow.report.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.lewindow.report.demo.cache.util.CacheEngine;
import com.lewindow.report.demo.cache.util.DiskCache;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView img = (ImageView) findViewById(R.id.img);

        DiskCache.getInstance().open(this);

        List<String> list = new ArrayList<>();
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502269267688&di=9577447fe9f8a1e841383f913128b36d&imgtype=0&src=http%3A%2F%2Fimg01.taopic.com%2F150305%2F318754-15030509413459.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502269267688&di=5c3f29195b08a2e6ab58e3c8e6e9ba1e&imgtype=0&src=http%3A%2F%2Fimg17.3lian.com%2Fd%2Ffile%2F201702%2F20%2Fb1b95fd7b88f9c3c08985ee4c3ecd9dc.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1502269267688&di=0728ed7f9e6358eff84a177df179e3d0&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F0%2F54801a8672cd1.jpg");

        CacheEngine.getInstance().getLocalImgs(list).subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] bytes) throws Exception {
                Log.d(com.lewindow.report.demo.cache.util.TAG.TAG, "最后任务输出结果： " + bytes);
                byte[] data = bytes;
                //这个可以在webview 里的MyWebClient -> shouldInterceptRequest 方法inputStrean返回
//                        ByteArrayInputStream stream = new ByteArrayInputStream(data);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                img.setImageBitmap(bitmap);
            }
        });

        /*CacheEngine.getInstance()
                .getLocalImg("http://oss.image.lex.lenovo.com.cn/card/2017/8/7/c8a9116f94254875a9f0f7d9fb8959da.jpg")
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) throws Exception {
                        Log.d(com.lewindow.report.demo.cache.util.TAG.TAG, "最后任务输出结果： " + bytes);
                        byte[] data = bytes;
                        //这个可以在webview 里的MyWebClient -> shouldInterceptRequest 方法inputStrean返回
//                        ByteArrayInputStream stream = new ByteArrayInputStream(data);

                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bitmap);
                    }
                });*/

    }
}
