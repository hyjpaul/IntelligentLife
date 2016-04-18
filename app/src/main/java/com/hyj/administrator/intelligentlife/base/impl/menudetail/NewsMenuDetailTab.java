package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hyj.administrator.intelligentlife.domain.News;


/**
 * 菜单详情页-新闻 NewsMenuDetailPager 中的ViewPager的页签页面对象
 */
public class NewsMenuDetailTab {

    private News.NewsTabData mTabData;// 单个页签的网络数据

    public Activity mActivity;
    public View mRootView;

    public NewsMenuDetailTab(Activity activity, News.NewsTabData newsTabData) {
        mActivity = activity;
        mTabData = newsTabData;
        mRootView = initView();
    }

    public View initView() {
        TextView view = new TextView(mActivity);
        view.setText(mTabData.title);
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);
        return view;
    }

    public void initData() {

    }
}
