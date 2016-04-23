package com.hyj.administrator.intelligentlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 显示新闻内容
 */
public class NewsContentActivity extends Activity implements View.OnClickListener {

    @ViewInject(R.id.ll_control)
    private LinearLayout llControl;
    @ViewInject(R.id.btn_back)
    private ImageButton btnBack;
    @ViewInject(R.id.btn_textsize)
    private ImageButton btnTextSize;
    @ViewInject(R.id.btn_share)
    private ImageButton btnShare;
    @ViewInject(R.id.btn_menu)
    private ImageButton btnMenu;
    @ViewInject(R.id.pg_lodding)
    private ProgressBar progressBar;
    @ViewInject(R.id.wv_news)
    private WebView mWebView;

    private String newsUrl;//新闻内容连接

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news);

        ViewUtils.inject(this);

        llControl.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.GONE);

        btnBack.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        newsUrl = getIntent().getStringExtra("newsUrl");
        mWebView.loadUrl(newsUrl);

        WebSettings settings = mWebView.getSettings();
//        settings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持,加载到手机的网页一般都是适配好屏幕的)
//        settings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
        settings.setJavaScriptEnabled(true);// 支持js功能

        mWebView.setWebViewClient(new WebViewClient() {
            // 开始加载网页
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // System.out.println("开始加载网页了");
                progressBar.setVisibility(View.VISIBLE);
            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //System.out.println("网页加载结束");
                progressBar.setVisibility(View.GONE);
            }

            // 所有链接跳转会走此方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //System.out.println("跳转链接:" + url);
                view.loadUrl(url);// 在跳转链接时强制在当前webview中加载
                return true;
            }
        });
        // mWebView.goBack();//跳到上个页面
        // mWebView.goForward();//跳到下个页面


        mWebView.setWebChromeClient(new WebChromeClient() {
            //            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                super.onReceivedTitle(view, title);
//                // 网页标题
//                System.out.println("网页标题:" + title);
//            }
            // 进度发生变化
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //  System.out.println("进度:" + newProgress);
                progressBar.setProgress(newProgress);
            }


        });
    }

    //点击顶部三个按钮
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_textsize:
                // 修改网页字体大小
                showChooseDialog();
                break;

            case R.id.btn_share:
                showShare();
                break;

            default:
                break;
        }
    }

    private int mTempWhich;// 记录临时选择的字体大小(点击确定之前)

    private int mCurrenWhich = 2;// 记录当前选中的字体大小(点击确定之后), 默认正常字体

    //展示选择字体大小的弹窗
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体设置");

        String[] items = new String[]{"超大号字体", "大号字体", "正常字体", "小号字体",
                "超小号字体"};

        builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTempWhich = which;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich) {
                    case 0:
                        // 超大字体
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        // settings.setTextZoom(22);
                        break;
                    case 1:
                        // 大字体
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        // 正常字体
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        // 小字体
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4:
                        // 超小字体
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;

                    default:
                        break;
                }
                mCurrenWhich = mTempWhich;
            }
        });

        builder.setNegativeButton("取消", null);

        builder.show();

    }

    //分享按钮：sharesdk
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("我是分享标题");

        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }
}
