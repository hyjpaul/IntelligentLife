package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.base.BaseMenuDetailPager;
import com.hyj.administrator.intelligentlife.domain.News;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;


/**
 * 菜单详情页-新闻  根据菜单上的新闻按钮替换新闻中心页NewsCenterPager
 *
 * @author Kevin
 * @date 2015-10-18
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager  {

    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;//通过TextUtil注入的方式，相当于findviewbyid

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    //NewsCenterPager请求服务器返回的数据，在new NewsMenuDetailPager的时候传给构造方法
    private ArrayList<News.NewsTabData> mTabData;// 页签网络数据
    private ArrayList<NewsMenuDetailTab> mPagers;// 页签页面集合

    public NewsMenuDetailPager(Activity activity, ArrayList<News.NewsTabData> children) {
        super(activity);
        mTabData = children;
    }

    @Override
    public View initView() {
//		TextView view = new TextView(mActivity);
//		view.setText("菜单详情页-新闻");
//		view.setTextColor(Color.RED);
//		view.setTextSize(22);
//		view.setGravity(Gravity.CENTER);

        View view = View.inflate(mActivity, R.layout.news_menu_detail, null);
        ViewUtils.inject(this, view);
        return view;
    }


    class NewsMenuDetailAdapter extends PagerAdapter {

        // 指定指示器的标题
        @Override
        public CharSequence getPageTitle(int position) {
            News.NewsTabData data = mTabData.get(position);
            return data.title;
        }

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
            NewsMenuDetailTab pager = mPagers.get(position);

            View view = pager.mRootView;

            container.addView(view);

            pager.initData();

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }
    }


    public void initData() {
        // 初始化页签
        mPagers = new ArrayList<NewsMenuDetailTab>();
        for (int i = 0; i < mTabData.size(); i++) {
            NewsMenuDetailTab pager = new NewsMenuDetailTab(mActivity, mTabData.get(i));
            mPagers.add(pager);
        }

        mViewPager.setAdapter(new NewsMenuDetailAdapter());

        mIndicator.setViewPager(mViewPager);// 将viewpager和指示器绑定在一起.注意:必须在viewpager设置完数据之后再绑定

    }

}
