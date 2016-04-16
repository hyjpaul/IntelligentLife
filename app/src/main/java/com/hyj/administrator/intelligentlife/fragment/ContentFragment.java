package com.hyj.administrator.intelligentlife.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.SlideActivity;
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
    private RadioGroup mRadioGroup;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_content, null);
        mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.rg_group);

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

        // 底栏标签切换监听
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_home:
                        // 首页
                        // mViewPager.setCurrentItem(0);
                        mViewPager.setCurrentItem(0, false);// 参2:表示是否具有滑动动画
                        break;
                    case R.id.rb_news:
                        // 新闻中心
                        mViewPager.setCurrentItem(1, false);
                        break;
                    case R.id.rb_smart:
                        // 智慧服务
                        mViewPager.setCurrentItem(2, false);
                        break;
                    case R.id.rb_gov:
                        // 政务
                        mViewPager.setCurrentItem(3, false);
                        break;
                    case R.id.rb_setting:
                        // 设置
                        mViewPager.setCurrentItem(4, false);
                        break;

                    default:
                        break;
                }
            }
        });

        //viewpager会默认加载下一个页面，为了节省流量和性能，当onPageSelected的时候才加载对应标签页
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mPagers.get(position);
                pager.initData();

                if (position == 0 || position == mPagers.size() - 1) {
                    // 首页和设置页要禁用侧边栏
                    setSlidingMenuEnable(false);
                } else {
                    // 其他页面开启侧边栏
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 手动加载第一页数据
        mPagers.get(0).initData();
        // 首页禁用侧边栏
        setSlidingMenuEnable(false);
    }


    /**
     * 开启或禁用侧边栏
     *
     * @param enable
     */
    protected void setSlidingMenuEnable(boolean enable) {
        // 获取侧边栏对象
        SlideActivity mainUI = (SlideActivity) mActivity;

        if (enable) {
            mainUI.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mainUI.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    //ViewPager的适配器
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
            View view = pager.mRootView;// 获取当前页面对象的原始布局也就是一个空的FrameLayout

            // pager.initData();// 初始化数据, viewpager会默认加载下一个页面,
            // 为了节省流量和性能,不要在此处调用初始化数据的方法,而是当viewpager onPageSelected的时候加载对应标签页

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    // 获取新闻中心页面
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) mPagers.get(1);//第二页是新闻中心页
    }
}
