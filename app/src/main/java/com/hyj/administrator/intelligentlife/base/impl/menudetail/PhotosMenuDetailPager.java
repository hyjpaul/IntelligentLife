package com.hyj.administrator.intelligentlife.base.impl.menudetail;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hyj.administrator.intelligentlife.R;
import com.hyj.administrator.intelligentlife.base.BaseMenuDetailPager;
import com.hyj.administrator.intelligentlife.domain.PhotosBean;
import com.hyj.administrator.intelligentlife.global.GlobalConstants;
import com.hyj.administrator.intelligentlife.utils.CacheUtil;
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
 * 菜单详情页-组图  根据菜单上的组图按钮替换新闻中心页NewsCenterPager
 *
 * @author Kevin
 * @date 2015-10-18
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager {
    @ViewInject(R.id.lv_photo)
    private ListView lvPhoto;
    @ViewInject(R.id.gv_photo)
    private GridView gvPhoto;

    private ArrayList<PhotosBean.PhotoNews> mPhotoNewsList;

    public PhotosMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
//		TextView view = new TextView(mActivity);
//		view.setText("菜单详情页-组图");
//		view.setTextColor(Color.RED);
//		view.setTextSize(22);
//		view.setGravity(Gravity.CENTER);

        //FrameLayout里包含ListView和GridView可通过隐藏和显示切换
        View view = View.inflate(mActivity, R.layout.photos_menu_detail, null);
        ViewUtils.inject(this, view);
        return view;
    }

    public void initData() {
        String cache = CacheUtil.getCache(GlobalConstants.PHOTOS_URL, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }

        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String json = responseInfo.result;
                processData(json);

                CacheUtil.setCache(GlobalConstants.PHOTOS_URL, json,
                        mActivity);
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
        PhotosBean photosBean = gson.fromJson(json, new TypeToken<PhotosBean>() {
        }.getType());

        mPhotoNewsList = photosBean.data.news;

        lvPhoto.setAdapter(new PhotoAdapter());
    }


    private class PhotoAdapter extends BaseAdapter {
//        private final MyBitmapUtils myBitmapUtils;

        private final BitmapUtils mBitmapUtils;

        public PhotoAdapter() {
            //使用BitmapUtils下载并缓存网络组图
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.topnews_item_default);

            //使用自定义的三级缓存下载组图,自定义的太简单很多时候还是可能造成内存溢出，实际开发还是用BitmapUtils
//            myBitmapUtils = new MyBitmapUtils();

        }

        @Override
        public int getCount() {
            return mPhotoNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mPhotoNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.photos_list_item, null);
                holder = new ViewHolder();
                holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            PhotosBean.PhotoNews item = (PhotosBean.PhotoNews) getItem(position);

            mBitmapUtils.display(holder.ivPic, item.listimage);
//            myBitmapUtils.display(holder.ivPic, item.listimage);

            holder.tvTitle.setText(item.title);

            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
    }

}
