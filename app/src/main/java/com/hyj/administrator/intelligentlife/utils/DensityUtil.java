package com.hyj.administrator.intelligentlife.utils;

import android.content.Context;

/**
 * 屏幕适配：dx、dp转换
 */
public class DensityUtil {

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context,float dpValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*density+0.5f);//加0.5是为了int类型的四舍五入
    }

    //根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dip(Context context,float pxValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/density+0.5f);

    }
}
