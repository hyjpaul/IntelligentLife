package com.hyj.administrator.intelligentlife.utils.mybitmaputils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hyj.administrator.intelligentlife.R;

/**
 * 自定义三级缓存图片加载工具  内存、本地、网络
 */
public class MyBitmapUtils {
    private NetCacheUtil mNetCacheUtil;
    private LocalCacheUtil mLocalCacheUtil;
    private MemoryCacheUtil mMemoryCacheUtil;

    public MyBitmapUtils() {

        mLocalCacheUtil = new LocalCacheUtil();
        mMemoryCacheUtil = new MemoryCacheUtil();
        mNetCacheUtil = new NetCacheUtil(mLocalCacheUtil, mMemoryCacheUtil);
    }

    public void display(ImageView imageView, String url) {
        // 设置默认图片
        imageView.setImageResource(R.drawable.pic_item_list_default);

// 优先从内存中加载图片, 速度最快, 不浪费流量

        Bitmap bitmap = mMemoryCacheUtil.getMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
//            System.out.println("从内存加载图片啦");
            return;
        }

        // 其次从本地(sdcard)加载图片, 速度快, 不浪费流量
        bitmap = mLocalCacheUtil.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
//            System.out.println("从本地加载图片啦");
            return;
        }

        // 最后从网络下载图片, 速度慢, 浪费流量
        mNetCacheUtil.getBitmapFromNet(imageView, url);

    }
}
