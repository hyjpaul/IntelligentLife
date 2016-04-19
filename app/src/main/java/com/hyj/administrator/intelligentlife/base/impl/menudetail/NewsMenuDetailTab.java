package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.domain.News;
import com.hyj.administrator.intelligentlife.domain.NewsTabBean;
import com.hyj.administrator.intelligentlife.global.GlobalConstants;
import com.hyj.administrator.intelligentlife.utils.CacheUtil;
import com.hyj.administrator.intelligentlife.view.TopNewsViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;


/**
 * 菜单详情页-新闻 NewsMenuDetailPager 中的ViewPager的页签页面对象
 * <p>
 * * ViewPagerIndicator使用流程: 1.引入库 2.解决support-v4冲突(让两个版本一致) 3.从例子程序中拷贝布局文件
 * 4.从例子程序中拷贝相关代码(指示器和viewpager绑定; 重写getPageTitle返回标题) 5.在清单文件中增加样式 6.背景修改为白色
 * 7.修改样式-背景样式&文字样式
 */
public class NewsMenuDetailTab {

    private News.NewsTabData mTabData;// 单个页签的网络数据

    private String mUrl;

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;

    public Activity mActivity;
    public View mRootView;

    @ViewInject(R.id.vp_top_news)
    public TopNewsViewPager mViewPager;

    public NewsMenuDetailTab(Activity activity, News.NewsTabData newsTabData) {

        mActivity = activity;
        mTabData = newsTabData;
        mRootView = initView();
        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    public View initView() {
        // 要给帧布局填充布局对象
//        TextView view = new TextView(mActivity);
//        view.setText(mTabData.title);
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);
//        return view;
        View view = View.inflate(mActivity, R.layout.news_menu_detail_tab, null);
        ViewUtils.inject(this,view);
        return view;
    }

    public void initData() {

        String cache = CacheUtil.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }

        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils util = new HttpUtils();
        util.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                String result = (String) responseInfo.result;
                processData(result);

                CacheUtil.setCache(mUrl, result, mActivity);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processData(String json) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(json, NewsTabBean.class);

        mTopNews = newsTabBean.data.topnews;
        if (mTopNews != null) {
            mViewPager.setAdapter(new TopNewsAdapter());
        }

    }

    class TopNewsAdapter extends PagerAdapter {
        private BitmapUtils mBitmapUtils;

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
        }

        @Override

        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imgView = new ImageView(mActivity);
//            imgView.setImageResource(R.drawable.topnews_item_default);
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片缩放方式, 宽高填充父控件

            String imageUrl = mTopNews.get(position).topimage;// 图片下载链接

            // 下载图片-将图片设置给imageview-避免内存溢出-缓存
            // BitmapUtils-XUtils自动下载网络并缓存
            mBitmapUtils.display(imgView,imageUrl);

            container.addView(imgView);
            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
