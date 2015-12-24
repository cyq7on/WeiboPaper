package com.xihua.weibopaper.utils;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.xihua.weibopaper.activity.R;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.utils
 * @Description:velloy图片显示的二次封装
 * @date 2015/12/2116:59
 */
public class ImageUtils {
    private ImageLoader imageLoader;
    private ImageLoader.ImageListener imageListener;
    private static ImageUtils instance;

    private ImageUtils() {

    }

    public static ImageUtils getInstance() {
        if (instance == null) {
            instance = new ImageUtils();
        }
        return instance;
    }

    public void setImageListener(ImageLoader.ImageListener imageListener) {
        this.imageListener = imageListener;
    }

    public void displayImage(RequestQueue requestQueue,String url,ImageView imageView) {
        if (imageListener == null) {
            imageListener = ImageLoader.getImageListener(imageView,
                    R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }
        imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        imageLoader.get(url,imageListener);
    }

    public void displayImage(RequestQueue requestQueue,String url,
                             ImageView imageView,int maxWith,int maxHight) {
        if (imageListener == null) {
            imageListener = ImageLoader.getImageListener(imageView,
                    R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        }
        imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        imageLoader.get(url,imageListener,maxWith,maxHight);
    }

    class BitmapCache implements ImageLoader.ImageCache {

        private LruCache<String, Bitmap> mCache;

        public BitmapCache() {
            int maxSize = 10 * 1024 * 1024;
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }

    }
}