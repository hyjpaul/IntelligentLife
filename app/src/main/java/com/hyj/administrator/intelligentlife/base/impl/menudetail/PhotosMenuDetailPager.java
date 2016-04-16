package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.hyj.administrator.intelligentlife.base.BaseMenuDetailPager;

/**
 * 菜单详情页-组图  根据菜单上的组图按钮替换新闻中心页NewsCenterPager
 * @author Kevin
 * @date 2015-10-18
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager {

	public PhotosMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView view = new TextView(mActivity);
		view.setText("菜单详情页-组图");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		view.setGravity(Gravity.CENTER);
		return view;
	}

}
