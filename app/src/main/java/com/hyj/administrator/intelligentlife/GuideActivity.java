package com.hyj.administrator.intelligentlife;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 新手引导页面
 */
public class GuideActivity extends Activity {
    private ViewPager mViewPager;
    private ArrayList<ImageView> mImageViewList;// imageView集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);

        initData();// 先初始化数据
        mViewPager.setAdapter(new GuideAdapter());// 设置数据
    }

    // 引导页图片id数组
    private int[] mImgIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    // 初始化数据
    private void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImgIds.length; i++) {
            ImageView imgView = new ImageView(this);
            imgView.setBackgroundResource(mImgIds[i]);// 通过设置背景,可以让宽高填充布局
            // view.setImageResource(resId)
            mImageViewList.add(imgView);
        }
    }

    //ViewPager的适配器
    class GuideAdapter extends PagerAdapter {
        // item的个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // 初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imgView = mImageViewList.get(position);
            container.addView(imgView);
            return imgView;
        }

        // 销毁item
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
