package com.hyj.administrator.intelligentlife.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.hyj.administrator.intelligentlife.base.BasePager;


/**
 * 新闻中心
 */
public class NewsCenterPager extends BasePager {


    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    public void initData() {
        //System.out.println("新闻中心初始化啦...");

        // 要给帧布局填充布局对象
        TextView view = new TextView(mActivity);
        view.setText("新闻中心");
        view.setTextColor(Color.RED);
        view.setTextSize(22);
        view.setGravity(Gravity.CENTER);

        mFlContent.addView(view);

    }
}
