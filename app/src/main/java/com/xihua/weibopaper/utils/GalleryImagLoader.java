package com.xihua.weibopaper.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xihua.weibopaper.activity.R;

import cn.finalteam.galleryfinal.widget.GFImageView;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.utils
 * @Description:相册的ImagLodaer
 * @date 2016/1/2214:57
 */
public class GalleryImagLoader implements cn.finalteam.galleryfinal.ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, final GFImageView imageView, Drawable defaultDrawable, int width, int height) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(com.xihua.weibopaper.activity.R.mipmap.ic_launcher)
//                .showImageForEmptyUri(R.mipmap.ic_launcher)
//                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        //用这个会造成图片显示混乱
//        ImageSize imageSize = new ImageSize(width, height);
//        com.nostra13.universalimageloader.core.ImageLoader.getInstance().
//                loadImage("file://" + path,imageSize,options,new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//                        imageView.setImageBitmap(loadedImage);
//                    }
//                });
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().
                displayImage("file://" + path, imageView, options);
    }

    @Override
    public void clearMemoryCache() {

    }
}
