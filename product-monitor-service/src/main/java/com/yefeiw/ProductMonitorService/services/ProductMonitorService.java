package com.yefeiw.ProductMonitorService.services;

import com.yefeiw.ProductMonitorService.domain.Ad;

import java.util.List;

/**
 * Created by vagrant on 7/26/17.
 */
public interface ProductMonitorService {
    //register Product in category
    //if category not found, create it
    void postCategory(String category);

    //get the recommended list for the user
    //user is denoted in uid
    void recommendForUser(String uid);

    //process all messages sent over rabbitMQ
    void process();



}
