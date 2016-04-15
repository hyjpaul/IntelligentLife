package com.hyj.administrator.intelligentlife.utils;

import android.content.Context;

/**
 * 网络缓存工具类，缓存的是json
 */
public class CacheUtil {

    //    以url为key, 以json为value,保存在本地
    public static void setCache(String url, String json, Context context) {
        //也可以用文件缓存: 以MD5把(url)转为一串数字作为文件名, 以json为文件内容
        SharedPreUtil.setString(context, url, json);

    }

    //    获取缓存
    public static String getCache(String url, Context context) {
        //也可以用文件缓存: 查找有没有一个文件叫做MD5(url)的, 有的话,说明有缓存
        return SharedPreUtil.getString(context, url, null);

    }
}
