package com.hyj.administrator.intelligentlife.fragment;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.base.BasePager;
import com.hyj.administrator.intelligentlife.base.impl.GovAffairsPager;
import com.hyj.administrator.intelligentlife.base.impl.HomePager;
import com.hyj.administrator.intelligentlife.base.impl.NewsCenterPager;
import com.hyj.administrator.intelligentlife.base.impl.SettingPager;
import com.hyj.administrator.intelligentlife.base.impl.SmartServicePager;
import com.hyj.administrator.intelligentlife.view.NoScrollViewPager;

import java.util.ArrayList;

/**
 * 主界面内容fragment
 */
public class ContentFragment extends BaseFragment {

    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mPagers;// 五个标签页的集合

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        return view;
    }

    @Override
    public void initData() {
        mPagers = new ArrayList<>();

        // 添加五个标签页
        mPagers.add(new HomePager(mActivity));
        mPagers.add(new NewsCenterPager(mActivity));
        mPagers.add(new SmartServicePager(mActivity));
        mPagers.add(new GovAffairsPager(mActivity));
        mPagers.add(new SettingPager(mActivity));

        mViewPager.setAdapter(new ContentAdapter());

    }


    private class ContentAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BasePager pager = mPagers.get(position);
            View view = pager.mRootView;// 获取当前页面对象的布局

            pager.initData();// 初始化数据

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
