package com.yefeiw;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yefeiw on 7/3/17.
 */
public class Ad implements Serializable{
    private static final long serialVersionUID = 1L;
    public int adId;
    public String asin;
    public List<String> keyWords;
    public String title; // required
    public double price; // required
    public String thumbnail; // required
    public String description; // required
    public String brand; // required
    public String detail_url; // required
    public String category;
    public long date;
}
