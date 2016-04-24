package com.hyj.administrator.intelligentlife.utils.mybitmaputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 本地缓存
 */
public class LocalCacheUtil {

    private static final String LOCAL_CACHE_URL =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/zutu_cache";//mnt/sdcard/zutu_cache

    // 写本地缓存
    public void setLocalCache(String url, Bitmap bitmap) {
        File dir = new File(LOCAL_CACHE_URL);

        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdir();// 创建文件夹
        }

        try {
            String filename = MD5Encoder.encode(url);//MD5加密后的url作为文件名

            File cacheFile = new File(LOCAL_CACHE_URL, filename);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(cacheFile));// 参1:图片格式;参2:压缩比例0-100; 参3:输出流
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 读本地缓存
    public Bitmap getLocalCache(String url) {
        try {
            File cacheFile = new File(LOCAL_CACHE_URL, MD5Encoder.encode(url));

            if (cacheFile.exists()) {

                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));

                return bitmap;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
