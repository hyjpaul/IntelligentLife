package com.hyj.administrator.intelligentlife.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyj.administrator.intelligentlife.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 自定义下拉刷新的listview
 */
public class PullToRefreshListView extends ListView {

    private static final int STATE_PULL_TO_REFRESH = 1;//下拉刷新状态
    private static final int STATE_RELEASE_TO_REFRESH = 2;//松开刷新状态
    private static final int STATE_REFRESHING = 3;//正在刷新状态

    private int mCurrentState = STATE_PULL_TO_REFRESH;// 当前刷新状态

    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;

    private RotateAnimation animUp;//箭头向上动画
    private RotateAnimation animDown;//箭头向下动画
    private ProgressBar pbProgress;

    private View mHeaderView;
    private int mHeaderViewHeight;//测量到了头布局高度
    private int startY = -1;//点到了ListView的ViewPager头布局而不是ListView

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
    }

    // 初始化头布局
    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh, null);
        this.addHeaderView(mHeaderView);

        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);

        // 隐藏头布局
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        //初始化箭头动画
        initAnim();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 当用户按住头条新闻的viewpager进行下拉时,ACTION_DOWN会被viewpager消费掉,导致startY没有赋值,此处需要重新获取一下
                if (startY == -1) {
                    startY = (int) ev.getY();
                }

                int endY = (int) ev.getY();
                int dy = endY - startY;//移动距离

                // 当前显示的第一个item的位置
                int firstVisiblePosition = getFirstVisiblePosition();

                // 必须下拉,并且当前显示的是第一个item
                if (dy > 0 && firstVisiblePosition == 0) {

                    int padding = dy - mHeaderViewHeight;
                    mHeaderView.setPadding(0, padding, 0, 0);// 计算当前下拉控件的padding值

                    //往下拉没有超出头布局的高度mHeaderViewHeight，并且状态不是松开，则要改成松开
                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        //继续往下拉，超出了，并且状态不是下拉，则要改成下拉
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }

                    return true;//需要自已处理下拉空间所以true，因为ListView默认的下拉什么都没做
                }
                break;

            case MotionEvent.ACTION_UP:
                startY = -1;
                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();

                    // 完整展示头布局
                    mHeaderView.setPadding(0, 0, 0, 0);

                    // 4. 进行回调
                    if (mRefreshListener != null) {
                        mRefreshListener.onRefresh();
                    }

                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    // 隐藏头布局
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //初始化箭头动画
    private void initAnim() {
        //向上箭头动画
        animUp = new RotateAnimation(0, -180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);//动画完保持住状态

        animDown = new RotateAnimation(-180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);//动画完保持住状态

    }

    //    根据当前状态刷新界面
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");
                ivArrow.clearAnimation();// 清除箭头动画,否则无法隐藏
                pbProgress.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    // 设置刷新时间
    private void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());

        tvTime.setText(time);
    }

    //刷新结束,收起控件(在父类的getDataFromServer请求完数据时调用)
    public void onRefreshComplete(boolean success) {

        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        //重置
        mCurrentState = STATE_PULL_TO_REFRESH;
        tvTitle.setText("下拉刷新");
        pbProgress.setVisibility(View.INVISIBLE);
        ivArrow.setVisibility(View.VISIBLE);

        if (success) {// 只有刷新成功之后才更新时间
            setCurrentTime();
        }
    }


    //当鼠标松开的时候(Action_up)，需要出现正在刷新...的Progress同时要向服务器获取数据，但获取数据的getDataFromServer()方法在父类TabDetailPager里
    //父类里  lvList.setOnRefreshListener(new OnRefreshListener() {
//    @Override
//    public void onRefresh() {
//        // 刷新数据
//        getDataFromServer();
//    }
//});
//    此时new的匿名内部类对象和传进2里，与mRefreshListener对应
    //然后子类mRefreshListener.onRefresh()相当于调了父类的onRefresh()刷新数据，子类还可以在onRefresh()里传参到父类


    // 3. 定义成员变量,接收监听对象
    private OnRefreshListener mRefreshListener;

    //    2. 暴露接口,相当于设置监听
    public void setOnRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    //    1. 下拉刷新的回调接口
    public interface OnRefreshListener {
        public void onRefresh();
    }
}
