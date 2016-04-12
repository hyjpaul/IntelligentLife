package com.hyj.administrator.intelligentlife.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.hyj.administrator.intelligentlife.base.BasePager;

/**
 * 首页
 */
public class HomePager extends BasePager {

    public HomePager(Activity activity) {
        super(activity);
    }

    public void initData() {
        // 要给帧布局填充布局对象
        TextView txtView = new TextView(mActivity);
        txtView.setText("首页");
        txtView.setTextColor(Color.RED);
        txtView.setTextSize(22);
        txtView.setGravity(Gravity.CENTER);

        mFlContent.addView(txtView);

    }
}
