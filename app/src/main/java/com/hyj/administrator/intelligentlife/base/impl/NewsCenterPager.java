package com.hyj.administrator.intelligentlife.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyj.administrator.intelligentlife.base.BasePager;
import com.hyj.administrator.intelligentlife.domain.News;
import com.hyj.administrator.intelligentlife.global.GlobalConstants;
import com.hyj.administrator.intelligentlife.utils.CacheUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {
private News mNews;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    public void initData()  {
        //System.out.println("新闻中心初始化啦...");
        // 要给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        mFlContent.addView(view);

        // 先判断有没有缓存,如果有的话,就加载缓存
        String cacheJson = CacheUtil.getCache(GlobalConstants.CATEGORY_URL, mActivity);

        if (!TextUtils.isEmpty(cacheJson)) {
            System.out.println("发现缓存啦...");
            processData(cacheJson);
        } else {
            // 请求服务器,获取数据
            // 开源框架: XUtils
            getDataFromServer();
        }
    }

    //从服务器获取数据 需要权限:<uses-permission android:name="android.permission.INTERNET"
    private void getDataFromServer() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // 请求成功
                        String result = responseInfo.result;// 获取服务器返回结果
                        Log.i("服务器返回结果:" , result);

                        // JsonObject, Gson解析JSON数据
                        processData(result);

                        // 写缓存
                        CacheUtil.setCache(GlobalConstants.CATEGORY_URL, result, mActivity);
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        // 请求失败
                        e.printStackTrace();
                        Toast.makeText(mActivity, s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    //GSON解析服务器JSON数据
    private void processData(String json) {
        Gson gson = new Gson();
        mNews = gson.fromJson(json, News.class);
        System.out.println("解析结果:"+mNews.toString());

    }
}
