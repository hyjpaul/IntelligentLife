package com.hyj.administrator.intelligentlife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hyj.administrator.intelligentlife.utils.DensityUtil;
import com.hyj.administrator.intelligentlife.utils.SharedPreUtil;

import java.util.ArrayList;

/**
 * 新手引导页面
 */
public class GuideActivity extends Activity {
    private ViewPager mViewPager;
    private ArrayList<ImageView> mImageViewList;// imageView集合
    private LinearLayout llContainer;
    private ImageView ivRedPoint;// 小红点
    // 小红点移动距离
    private int mPointDis;
    private Button mBtnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);
        ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
        mBtnStart = (Button) findViewById(R.id.btn_start);

        initData();// 先初始化数据
        mViewPager.setAdapter(new GuideAdapter());// 设置数据

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // 当页面滑动过程中的回调
//                System.out.println("当前位置:" + position + ";移动偏移百分比:"
//                        + positionOffset);
                // 更新小红点距离
                int leftMargin = (int) (mPointDis * positionOffset) + position
                        * mPointDis;// 计算小红点当前的左边距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint
                        .getLayoutParams();
                params.leftMargin = leftMargin;// 修改左边距

                // 重新设置布局参数
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                // 某个页面被选中
                if (position == mImageViewList.size() - 1) {// 最后一个页面显示开始体验的按钮
                    mBtnStart.setVisibility(View.VISIBLE);
                }else {
                    mBtnStart.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 页面状态发生变化的回调,开始滑动结束滑动状态都变化
            }
        });

        //不是重点，需要的时候要懂得copy
        // 计算两个圆点的距离
        // 移动距离=第二个圆点left值 - 第一个圆点left值
        // measure(测量)->layout(确定位置)->draw(activity的onCreate方法执行结束之后才会走此流程) 不管是自定义控件还是原生的都要在Activity上绘制，控件一般都直接拿开源框架
        // mPointDis = llContainer.getChildAt(1).getLeft()
        // - llContainer.getChildAt(0).getLeft();
        // System.out.println("圆点距离:" + mPointDis);

        // 监听layout方法结束的事件,位置确定好之后再获取圆点间距
        // 视图树
        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 移除监听,避免重复回调
                        ivRedPoint.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        // ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        // layout方法执行结束的回调
                        mPointDis = llContainer.getChildAt(1).getLeft()
                                - llContainer.getChildAt(0).getLeft();
                        System.out.println("圆点距离:" + mPointDis);
                    }
                });

        mBtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //更新sp, 当按下开始体验按钮就表示，以后再进入应用就不是第一次进入了
                SharedPreUtil.setBoolean(GuideActivity.this, "isFirstEnter", false);

                //跳到主页面
                Intent intent = new Intent(GuideActivity.this, SlideActivity.class);
                startActivity(intent);
                finish();
            }
        });
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

            // 初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);// 设置图片(shape形状)

            // 初始化布局参数, 宽高包裹内容,父控件是谁,就是谁声明的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                // 从第二个点开始设置左边距
                params.leftMargin = DensityUtil.dip2px(this,10);
            }

            point.setLayoutParams(params);// 设置布局参数

            llContainer.addView(point);// 给容器添加圆点
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
