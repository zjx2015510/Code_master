package com.wxxiaomi.ming.webviewcachemodule.common;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.wxxiaomi.ming.webviewcachemodule.R;
import com.yancy.gallerypick.inter.ImageLoader;
import com.yancy.gallerypick.widget.GalleryImageView;


/**
 * Created by 12262 on 2016/11/23.
 * 加载图片的工具
 */
public class GlideImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.gallery_pick_photo)
                .centerCrop()
                .into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}
