package com.hyj.administrator.intelligentlife.domain;

import java.util.ArrayList;

/**
 * 组图对象
 */
public class PhotosBean {
    public PhotosData data;

    public class PhotosData {
        public ArrayList<PhotoNews> news;
    }

    public class PhotoNews {
        public int id;
        public String listimage;
        public String title;
    }
}
