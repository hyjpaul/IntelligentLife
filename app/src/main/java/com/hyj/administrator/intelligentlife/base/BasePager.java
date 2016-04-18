package com.hyj.administrator.intelligentlife.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.hyj.administrator.intelligentlife.R;

/**
 * 五个ViewPager标签页的基类
 */
public class BasePager {
    public Activity mActivity;

    public FrameLayout mFlContent;// 空的帧布局对象, 要动态添加布局

    public View mRootView;// 当前页面的布局对象


    public BasePager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }

    // 初始化布局
    public View initView() {
        View view = View.inflate(mActivity, R.layout.tab_base_pager, null);
        mFlContent = (FrameLayout) view.findViewById(R.id.fl_content);
        return view;
    }

    // 初始化数据
    public void initData() {

    }

}
