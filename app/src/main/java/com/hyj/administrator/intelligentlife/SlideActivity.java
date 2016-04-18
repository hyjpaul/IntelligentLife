package com.hyj.administrator.intelligentlife;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hyj.administrator.intelligentlife.base.impl.NewsCenterPager;
import com.hyj.administrator.intelligentlife.fragment.ContentFragment;

/**
 * 主界面
 */
public class SlideActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //给主界面faragment一个标签
    private static final String TAG_CONTENT = "TAG_CONTENT";

    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //初始化fragment
        initFragment();
    }

    //初始化fragment
    private void initFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();// 开始事务
        transaction.replace(R.id.fl_main, new ContentFragment(), TAG_CONTENT);// 用fragment替换帧布局;参1:帧布局容器的id;参2:是要替换的fragment;参3:标记,有了标记就可以	// Fragment fragment =  // fm.findFragmentByTag(TAG_LEFT_MENU);//根据标记找到对应的fragment
        transaction.commit();
    }

    //通过ContentFragment得到新闻中心页NewsCenterPager然后设置新闻中心的菜单详情页，通过导航栏点击切换菜单
    protected void setCurrentMenuDetailPager(int position) {
        // 获取ContentFragment
        ContentFragment fragment = (ContentFragment) getSupportFragmentManager()
                .findFragmentByTag(TAG_CONTENT);// 根据标记找到对应的fragment对象

        NewsCenterPager pager = fragment.getNewsCenterPager();

        pager.setCurrentMenuDetailPager(position);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.slide, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //重绘了下标题栏，解决挤压问题
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            // 侧边栏点击之后, 要修改新闻中心的FrameLayout中的内容
            setCurrentMenuDetailPager(0);

        } else if (id == R.id.nav_topic) {
            setCurrentMenuDetailPager(1);

        } else if (id == R.id.nav_picture) {
            setCurrentMenuDetailPager(2);

        } else if (id == R.id.nav_share) {
            setCurrentMenuDetailPager(3);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
