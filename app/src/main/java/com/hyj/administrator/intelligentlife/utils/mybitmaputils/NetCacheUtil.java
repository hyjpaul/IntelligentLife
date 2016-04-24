package com.hyj.administrator.intelligentlife.utils.mybitmaputils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存
 */
public class NetCacheUtil {

    private ImageView imageView;
    private String bitmapUrl;

    private LocalCacheUtil mLocalCacheUtil;
    private MemoryCacheUtil mMemoryCacheUtil;

    public NetCacheUtil(LocalCacheUtil localCacheUtil,MemoryCacheUtil memoryCacheUtil) {
        mLocalCacheUtil = localCacheUtil;
        mMemoryCacheUtil = memoryCacheUtil;
    }


    public void getBitmapFromNet(ImageView imageView, String url) {
        // AsyncTask 异步封装的工具, 可以实现异步请求及主界面更新(对线程池+handler的封装)
        new BitmapTask().execute(imageView, url);// 启动AsyncTask
    }

    class BitmapTask extends AsyncTask<Object, Integer, Bitmap> {
        // 1.预加载, 运行在主线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // 3.更新进度的方法, 运行在主线程
        @Override
        protected void onProgressUpdate(Integer... values) {
            // 更新进度条
            super.onProgressUpdate(values);
        }

        // 2.正在加载, 运行在子线程(核心方法), 可以直接异步请求,底层源码是在子线程回调的这个方法所以说运行在子线程
        @Override
        protected Bitmap doInBackground(Object... params) {
            imageView = (ImageView) params[0];
            bitmapUrl = (String) params[1];

            // 开始下载图片
            Bitmap bitmap = download(bitmapUrl);

            // publishProgress(values) 调用此方法实现进度更新(会回调onProgressUpdate)

            return bitmap;
        }

        // 4.加载结束, 运行在主线程(核心方法), 可以直接更新UI
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setTag(bitmapUrl);// 打标记, 将当前imageview和url绑定在了一起

            if (result != null) {
                // 给imageView设置图片
                // 由于listview的重用机制导致imageview对象可能被多个item共用,
                // 从而可能将错误的图片设置给了imageView对象
                // 所以需要在此处校验, 判断是否是正确的图片
                String url = (String) imageView.getTag();

                if (url.equals(bitmapUrl)) {// 判断图片绑定的url是否就是当前bitmap的url, 如果是,说明图片正确

                    imageView.setImageBitmap(result);

                    // 先写到本地缓存
                    mLocalCacheUtil.setLocalCache(url,result);

                    //再写内存缓存
                    mMemoryCacheUtil.setMeoryCache(url,result);
                }
            }

            super.onPostExecute(result);
        }
    }

    // 从网络下载图片
    private Bitmap download(String bitmapUrl) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(bitmapUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 连接超时
            conn.setReadTimeout(5000);// 连接超时

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                InputStream inputStream = conn.getInputStream();

                // 根据输入流生成bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

}
