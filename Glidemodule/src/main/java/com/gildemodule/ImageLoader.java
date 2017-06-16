package com.gildemodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.yutils.YUtils;

import java.io.File;

/**
 * Created by yangc on 2017/5/7.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:  glide 加载封装
 */
public class ImageLoader {
    public static final String TAG = "ImageLoader";
    private int defaultRes;
    private int defaultError;

    public static ImageLoader getInstace() {
        return Holder.ImageLoader;
    }

    private ImageLoader() {

    }

    private static class Holder {
        static ImageLoader ImageLoader = new ImageLoader();
    }

    /***
     * 初始化日志
     *
     * @param context application
     * @param isDebug 开启调试模式,指示器
     * @param isLog   开启日志
     ***/
    public static void init(Context context, boolean isDebug, boolean isLog) {
//
    }

    /***
     * 设置默认图
     *
     * @param defaultRes   默认加载图
     * @param defaultError 默认失败图
     **/
    public void defaultImage(int defaultRes, int defaultError) {
        this.defaultRes = defaultRes;
        this.defaultError = defaultError;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).crossFade().centerCrop().placeholder(defaultError).error(defaultRes).into(imageView);
    }

    public void displayImage(Context context, String path, ImageView imageView, int defaultRes, int width, int height) {
        Glide.with(context).load(path).crossFade().override(width, height).centerCrop().placeholder(defaultRes).into(imageView);
    }


    public void displayImage(Context context, String path, ImageView imageView, int defaultRes, int defaultError, int width, int height) {
        Glide.with(context).load(path).crossFade().override(width, height).centerCrop().placeholder(defaultRes).error(defaultError).into(imageView);

    }

    public void displayImage(Context context, String path, ImageView imageView, int defaultRes, int defaultError) {
        Glide.with(context).load(path).crossFade().placeholder(defaultRes).error(defaultError).into(imageView);
    }

    public void displayImage(Context activity, String path, SimpleTarget<Bitmap> target) {
        DrawableTypeRequest glide;
        if (YUtils.isHttp(path)) {
            glide = Glide.with(activity).load(path);
        } else {
            glide = Glide.with(activity).load(new File(path));
        }
        glide.crossFade();
        glide.asBitmap()
                .placeholder(defaultRes)
                .error(defaultError)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(target);
    }

    //清理磁盘缓存 需要在子线程中执行
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }

    //清理内存缓存 可以在UI主线程中进行
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    /***
     * //监听onScrollStateChanged的时候调用执行 e 被暂停的给定tag的所有请求s
     **/
    public void resumeTag(Context context) {
        Glide.with(context).resumeRequests();
    }

    /***
     * 监听onScrollStateChanged的时候调用执行 滑动暂停加载图片
     **/
    public void pauseTag(Context context) {
        Glide.with(context).pauseRequests();
    }

    /***
     * 取消设置了给定tag的所有请求
     **/
    public void cancelTag(Context context) {
        Glide.with(context).onDestroy();
    }


}
