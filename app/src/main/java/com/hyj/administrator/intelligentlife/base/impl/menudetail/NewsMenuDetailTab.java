package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyj.administrator.intelligentlife.NewsContentActivity;
import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.domain.News;
import com.hyj.administrator.intelligentlife.domain.NewsTabBean;
import com.hyj.administrator.intelligentlife.global.GlobalConstants;
import com.hyj.administrator.intelligentlife.utils.CacheUtil;
import com.hyj.administrator.intelligentlife.utils.SharedPreUtil;
import com.hyj.administrator.intelligentlife.view.PullToRefreshListView;
import com.hyj.administrator.intelligentlife.view.TopNewsViewPager;
import com.hyj.administrator.viewpagerindicator.CirclePageIndicator;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * 菜单详情页-新闻 NewsMenuDetailPager 中的ViewPager的页签页面对象(服务器返回12个顶部Indicator的标题这里就有12个)
 * <p/>
 * * ViewPagerIndicator使用流程: 1.引入库 2.解决support-v4冲突(让两个版本一致) 3.从例子程序中拷贝布局文件
 * 4.从例子程序中拷贝相关代码(指示器和viewpager绑定; 重写getPageTitle返回标题) 5.在清单文件中增加样式 6.背景修改为白色
 * 7.修改样式-背景样式&文字样式
 */
public class NewsMenuDetailTab {

    private News.NewsTabData mTabData;// 单个页签的网络数据

    private String mUrl;//最开始的数据链接
    private String mMoreUrl;//下拉加载的下一页数据链接

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;

    public Activity mActivity;
    public View mRootView;

    //头条新闻
    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager mViewPager;

    //头条新闻标题
    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;

    //头条新闻小圆点snap
    @ViewInject(R.id.indicator_circle)
    private CirclePageIndicator mCirclePageIndicator;

    //新闻列表ListView
    @ViewInject(R.id.lv_list)
    private PullToRefreshListView mNewsListView;

    private NewsListAdapter mNewsListAdapter;

    private Handler mHandler;//用于头条新闻的图片轮播

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
        ViewUtils.inject(this, view);//注入ListView

        // 给新闻listview添加头布局
        final View mHeaderView = View.inflate(mActivity, R.layout.list_item_header, null);
        ViewUtils.inject(this, mHeaderView);// 此处必须将头布局也注入
        mNewsListView.addHeaderView(mHeaderView);

        // 5. 前端界面自定义ListView设置回调
        mNewsListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            //下拉刷新
            @Override
            public void onRefresh() {
                // 刷新数据
                getDataFromServer();

            }

            //上拉加载更多
            @Override
            public void onLoadMore() {
                // 判断是否有下一页数据
                if (mMoreUrl != null) {
                    // 有下一页
                    getMoreDataFromServer();
                } else {
                    // 没有下一页
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
                            .show();
                    // 没有数据时也要收起控件
                    mNewsListView.onRefreshComplete(true);
                }
            }
        });

        //点击ListView的item给已读新闻打上标记，并保存在本地
        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerCount = mNewsListView.getHeaderViewsCount();// 获取头布局数量
                position = position - headerCount;// ListView的Item位置需要减去头布局的占位
//                System.out.println("第" + position + "个被点击了");

                NewsTabBean.NewsData news = mNewsList.get(position);

                // read_ids: 1101,1102,1105,1203,
                String readIds = SharedPreUtil.getString(mActivity, "read_ids", "");

                if (!readIds.contains(news.id + "")) {// 避免重复添加同一个id,只有不包含当前id,才追加
                    readIds = readIds + news.id + ",";
                    SharedPreUtil.setString(mActivity, "read_ids", readIds);
                }

                // 要将被点击的item的新闻文字颜色改为灰色, 局部刷新, view对象就是当前被点击的ListView的item对象
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
                // mNewsAdapter.notifyDataSetChanged();//全局刷新, 也有同样效果但浪费性能

                Intent intent = new Intent(mActivity, NewsContentActivity.class);
                intent.putExtra("newsUrl", news.url);
                mActivity.startActivity(intent);

            }
        });

        return view;
    }

    public void initData() {

        String cache = CacheUtil.getCache(mUrl, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache, false);
        }

        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils util = new HttpUtils();
        util.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                String result = (String) responseInfo.result;
                processData(result, false);

                CacheUtil.setCache(mUrl, result, mActivity);

                // 收起下拉刷新控件,并刷新时间
                mNewsListView.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();

                // 收起下拉刷新控件,不刷新时间
                mNewsListView.onRefreshComplete(false);
            }
        });
    }

    //下拉加载下一页数据
    protected void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, true);//需要下拉加载更多数据传isMore为true

                // 收起下拉刷新控件
                mNewsListView.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // 请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                // 收起下拉刷新控件
                mNewsListView.onRefreshComplete(false);
            }
        });
    }

    private void processData(String json, boolean isMore) {
        Gson gson = new Gson();
        Type type = new TypeToken<NewsTabBean>() {
        }.getType();
        NewsTabBean newsTabBean = gson.fromJson(json, type);//tyep相当于NewsTabBean.class

        //下拉加载的下一页数据
        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }

        if (!isMore) {//如果没有加载下一页数据，正常填空数据

            // 头条新闻填充数据
            mTopNews = newsTabBean.data.topnews;
            if (mTopNews != null) {
                mViewPager.setAdapter(new TopNewsAdapter());
                mCirclePageIndicator.setViewPager(mViewPager);
                mCirclePageIndicator.setSnap(true);// 快照方式展示(小圆点)

                mCirclePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        // 更新头条新闻标题
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        mTvTitle.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                // 更新第一个头条新闻标题
                mTvTitle.setText(mTopNews.get(0).title);

                // 默认让第一个小圆点选中(解决页面销毁后重新初始化时,Indicator仍然保留上次圆点位置的bug)
                mCirclePageIndicator.onPageSelected(0);
            }

            // 列表新闻
            mNewsList = newsTabBean.data.news;
            if (mNewsList != null) {
                mNewsListAdapter = new NewsListAdapter();
                mNewsListView.setAdapter(mNewsListAdapter);
            }

            // 保证启动自动轮播逻辑只执行一次,也就是第一次进入的时候new一个handler,一次sendEmptyMessageDelayed,其他的时候handler不是空的所以跳过这段，但内部消息内循环了所以可以轮播图片,否则一直发消息会阻塞
            if (mHandler == null) {
                mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;

                        if (currentItem > mTopNews.size() - 1) {
                            currentItem = 0;// 如果已经到了最后一个页面,跳到第一页
                        }

                        mViewPager.setCurrentItem(currentItem);

                        mHandler.sendEmptyMessageDelayed(0, 3000);// 继续发送延时3秒的消息,形成内循环
                    }
                };

                mHandler.sendEmptyMessageDelayed(0, 3000);// 发送延时3秒的消息

                //当用户按住头条新闻图片时停止图片轮播，手放开继续轮播
                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
//                                System.out.println("ACTION_DOWN");
                                // 停止广告自动轮播
                                // 删除handler的所有消息
                                mHandler.removeCallbacksAndMessages(null);
                                // mHandler.post(new Runnable() {
                                //这个方法与callback有关具体怎么用还不知道
                                // @Override
                                // public void run() {
                                // //在主线程运行  收到消息不用在handlemessage处理了直接在这里
                                // }
                                // });
                                break;

                            case MotionEvent.ACTION_UP:
//                                System.out.println("ACTION_UP");
                                // 启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;

                            case MotionEvent.ACTION_CANCEL://事件取消时执行,当按下viewpager后还没抬起又直接去滑动listview这样抬起事件就被ListView拿去了,导致viewpager抬起事件无法响应被取消
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;

                            default:
                                break;
                        }
                        return false;//TODO: 为什么？和true以及super.什么区别
                    }
                });
            }
        } else {
            // 加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews);// 将数据追加在原来的集合中
            // 刷新listview
            mNewsListAdapter.notifyDataSetChanged();
        }

    }

    // 头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter {
        private BitmapUtils mBitmapUtils;

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            // 设置加载中的默认图片
            mBitmapUtils.configDefaultLoadFailedImage(R.drawable.topnews_item_default);
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
            mBitmapUtils.display(imgView, imageUrl);

            container.addView(imgView);
            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //新闻列表ListView适配器
    class NewsListAdapter extends BaseAdapter {

        private BitmapUtils bimapUtils;

        public NewsListAdapter() {
            bimapUtils = new BitmapUtils(mActivity);
            bimapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTabBean.NewsData news = (NewsTabBean.NewsData) getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);

            //根据本地记录来标记已读未读
            String readIds = SharedPreUtil.getString(mActivity, "read_ids", "");
            if (readIds.contains(news.id + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {//需要写else，因为convertView可能会在下一个未标记的ListView item布局中重用已标记布局
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            bimapUtils.display(holder.ivIcon, news.listimage);

            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

}
